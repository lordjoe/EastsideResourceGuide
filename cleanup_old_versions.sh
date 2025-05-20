#!/bin/bash

# CONFIGURATION
PROJECT_ID="lordjoehrd"
IMAGE_NAME="eastside-app"
GCR_PATH="gcr.io/${PROJECT_ID}/${IMAGE_NAME}"
VERSIONS_TO_KEEP=3

# Step 1: List all tagged image versions sorted by timestamp (descending)
echo "📋 Fetching image versions from ${GCR_PATH}..."
all_tags=$(gcloud container images list-tags ${GCR_PATH} \
  --filter='tags:*' \
  --sort-by='~timestamp' \
  --format='get(tags)' | tr ';' '\n')

# Step 2: Identify tags to delete (everything after the first $VERSIONS_TO_KEEP)
delete_tags=$(echo "$all_tags" | sed -n "$((VERSIONS_TO_KEEP+1)),\$p")

# Step 3: Delete old tags
if [[ -z "$delete_tags" ]]; then
  echo "✅ No old versions to delete. Only ${VERSIONS_TO_KEEP} or fewer found."
else
  echo "🗑 Deleting old image versions:"
  for tag in $delete_tags; do
    echo "  • $tag"
    gcloud container images delete "${GCR_PATH}:$tag" --quiet
  done
  echo "✅ Cleanup complete."
fi
