package com.lordjoe.resource_guide.model;

import com.lordjoe.resource_guide.Guide;
import com.lordjoe.resource_guide.Resource;
import com.lordjoe.resource_guide.dao.CommunityResourceDAO;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Draft tracker for "new resource" edit flows.
 * - Lives in model (per your rule).
 * - Never mutates Resource id (final).
 * - If a DB row gets created, we just record that persisted id so we can delete it on cancel.
 */
@org.springframework.stereotype.Service
public class ResourceDraftService {

    public static final class DraftInfo {
        public final String draftId;
        public final int parentId;
        public final Resource resourceRef;     // immutable id; might be 0 for drafts
        private volatile Integer persistedId;  // set if we inserted a DB row

        DraftInfo(String draftId, int parentId, Resource resourceRef) {
            this.draftId = draftId;
            this.parentId = parentId;
            this.resourceRef = resourceRef;
            this.persistedId = resourceRef.getId();
        }
        public Integer getPersistedId() { return persistedId; }
        public void setPersistedId(Integer persistedId) { this.persistedId = persistedId; }
    }

    private final Map<String, DraftInfo> drafts = new ConcurrentHashMap<>();

    /** Register a draft and return the draftId to round-trip in forms. */
    public String registerDraft(Resource resource, int parentId) {
        String id = UUID.randomUUID().toString();
        drafts.put(id, new DraftInfo(id, parentId, resource));
        return id;
    }

    /** Record that a DB row was created for this draft (do NOT modify Resource id). */
    public void markPersisted(String draftId, int persistedId) {
        DraftInfo d = drafts.get(draftId);
        if (d != null) d.setPersistedId(persistedId);
    }

    /**
     * Fully discard a draft:
     * 1) unlink from parent
     * 2) evict from Guide caches/indexes
     * 3) delete DB row if one exists for the draft
     */
    public DraftInfo discardDraft(String draftId, Guide guide ) {
        DraftInfo d = drafts.remove(draftId);
        if (d == null) return null;

        Resource r = d.resourceRef;
        CommunityResource cr = CommunityResource.getInstance(r.getId()) ;
        if(cr == null) {
            CommunityResource.dropInstance(cr);
            Guide.Instance.removeResource(cr);
        }
        synchronized (guide) {
            // 1) unlink from parent list
            guide.removeFromParent(r);

            // 2) evict from all caches/indexes (by id/name/etc.)
            //    Prefer an id-based eviction so it also works if id==0 (noop-safe).
            guide.evictResourceCachesById(r.getId());

            // 3) DB delete only if a persisted row was created
            Integer persistedId = d.getPersistedId();
            if (persistedId != null && persistedId > 0) {
                CommunityResourceDAO.deleteResourceCascade(persistedId);
            }
        }
        return d;
    }
}
