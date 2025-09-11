package com.lordjoe.sandhurst;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;

public class EditInhabitantPageMaker {

    public static String generate(Inhabitant inh, HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        String csrfParam = token != null ? token.getParameterName() : null;
        String csrfValue = token != null ? token.getToken() : null;

        // Null-safe values
        String name  = escape(inh.getName());
        String email = escape(inh.getEmail());
        String phone = escape(inh.getPhone());
        String type  = escape(inh.getType() != null ? inh.getType().name() : "Adult");

        // If you already expose an endpoint that serves the current photo, this will show it.
        // If not, it simply stays hidden (onerror hides it).
        String currentPhotoSrc = "/sandhurst/inhabitant/photo?id=" + inh.getId();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='utf-8'><title>Edit Inhabitant</title>")
                .append("<link rel='icon' type='image/x-icon' href='/favicon.ico'>")
                .append("<style>")
                .append("body{font-family:Arial,sans-serif;background:#f5f7fb;margin:0;padding:24px}")
                .append(".card{max-width:640px;margin:40px auto;background:#fff;border-radius:12px;")
                .append("box-shadow:0 8px 24px rgba(0,0,0,.08);padding:24px}")
                .append("label{display:block;font-weight:bold;margin:12px 0 6px}")
                .append("input,select{width:100%;padding:10px;border:1px solid #d4d7e1;border-radius:8px;font-size:15px;box-sizing:border-box}")
                .append(".row{display:flex;gap:10px}")
                .append(".row > *{flex:1}")
                .append(".actions{display:flex;gap:10px;margin-top:20px}")
                .append("button{padding:12px 16px;border:0;border-radius:10px;cursor:pointer;font-size:15px}")
                .append(".primary{background:#3f51b5;color:#fff}")
                .append(".secondary{background:#e9ecf6;color:#1a2240}")
                .append(".danger{background:#d32f2f;color:#fff}")
                .append(".photo-wrap{display:flex;gap:16px;align-items:flex-start;margin-top:8px}")
                .append(".avatar{width:96px;height:96px;object-fit:cover;border-radius:12px;border:1px solid #d4d7e1;display:none}")
                .append(".hint{font-size:12px;color:#555;margin-top:4px}")
                .append("</style>")
                .append("<script>")
                .append("function previewPhoto(input){")
                .append("  const img=document.getElementById('avatarPreview');")
                .append("  if(input.files && input.files[0]){")
                .append("    img.style.display='block';")
                .append("    img.src=URL.createObjectURL(input.files[0]);")
                .append("  }")
                .append("}")
                .append("</script>")
                .append("</head><body>")
                .append("<div class='card'>")
                .append("<h2>Edit Inhabitant</h2>");

        // --- UPDATE form (Save & Cancel both submit here) ---
        html.append("<form method='post' action='/sandhurst/updateInhabitant' autocomplete='on' enctype='multipart/form-data'>");
        if (csrfParam != null && csrfValue != null) {
            html.append("<input type='hidden' name='").append(escape(csrfParam)).append("' value='").append(escape(csrfValue)).append("'>");
        }

        html.append("<input type='hidden' name='id' value='").append(inh.getId()).append("'>")
                .append("<label for='name'>Name</label>")
                .append("<input id='name' name='name' type='text' required value='").append(name).append("'>")

                .append("<div class='row'>")
                .append("  <div><label for='email'>Email</label>")
                .append("  <input id='email' name='email' type='email' inputmode='email' autocomplete='email' value='").append(email).append("'></div>")
                .append("  <div><label for='phone'>Phone</label>")
                .append("  <input id='phone' name='phone' type='text' inputmode='tel' autocomplete='tel' value='").append(phone).append("'></div>")
                .append("</div>")

                .append("<label for='type'>Type</label>")
                .append("<select id='type' name='type'>")
                .append(option("Adult", type))
                .append(option("Child", type))
                .append(option("Pet", type))
                .append(option("Other", type))
                .append("</select>");

        // --- PHOTO upload + preview + remove toggle ---
        html.append("<label for='photo'>Picture</label>")
                .append("<div class='photo-wrap'>")
                .append("  <img id='avatarPreview' class='avatar' src='").append(escape(currentPhotoSrc)).append("' ")
                .append("       onload=\"this.style.display='block'\" ")
                .append("       onerror=\"this.style.display='none'\" alt='Current picture'>")
                .append("  <div style='flex:1'>")
                .append("    <input id='photo' name='photo' type='file' accept='image/*' onchange='previewPhoto(this)'>")
                .append("    <div class='hint'>Accepted: JPG/PNG, up to a few MB (server limits may apply)</div>")
                .append("    <label style='display:flex;align-items:center;gap:6px;margin-top:8px;'>")
                .append("      <input type='checkbox' name='removePhoto' value='true'> Remove existing picture")
                .append("    </label>")
                .append("  </div>")
                .append("</div>");

        // Actions: Save and Cancel both submit to the SAME endpoint.
        html.append("<div class='actions'>")
                .append("  <button class='primary' type='submit' name='action' value='save'>Save</button>")
                .append("  <button class='secondary' type='submit' name='action' value='cancel' title='Cancel changes (will still save)'>Cancel</button>")
                .append("</div>")
                .append("</form>");

        // --- DELETE form (separate, with confirm) ---
        html.append("<form method='post' action='/sandhurst/deleteInhabitant' ")
                .append("onsubmit=\"return confirm('Delete this inhabitant? This cannot be undone.');\" ")
                .append("style='margin-top:10px'>");
        if (csrfParam != null && csrfValue != null) {
            html.append("<input type='hidden' name='").append(escape(csrfParam)).append("' value='").append(escape(csrfValue)).append("'>");
        }
        html.append("<input type='hidden' name='id' value='").append(inh.getId()).append("'>")
                .append("<button class='danger' type='submit'>Delete</button>")
                .append("</form>");

        html.append("</div></body></html>");
        return html.toString();
    }

    private static String option(String value, String current) {
        boolean selected = value.equalsIgnoreCase(current);
        return "<option value='" + escape(value) + "'" + (selected ? " selected" : "") + ">" + escape(value) + "</option>";
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
