package com.lordjoe.sandhurst;

public class EditInhabitantPageMaker {

    public static String generate(Inhabitant inh) {
        House house =  inh.getHouse();

        StringBuilder out = new StringBuilder();
        out.append("<html><head><title>Edit Inhabitant</title>\n")
                .append("<style>\n")
                .append(".dropzone { width: 300px; height: 150px; border: 2px dashed #ccc; border-radius: 10px; display: flex; justify-content: center; align-items: center; margin: 10px 0; }\n")
                .append(".dropzone.hover { border-color: #666; }\n")
                .append("</style>\n")
                .append("</head><body>\n");

        out.append("<h1>Edit Inhabitant: ").append(inh.getName()).append("</h1>\n");

        // Inhabitant info form
        out.append("<form action='/sandhurst/updateInhabitant' method='POST'>\n")
                .append("<input type='hidden' name='id' value='").append(inh.getId()).append("'/>\n")
                .append("Name: <input type='text' name='name' value='").append(inh.getName()).append("'/><br/>\n")
                .append("Phone: <input type='text' name='phone' value='").append(inh.getPhone() != null ? inh.getPhone() : "").append("'/><br/>\n")
                .append("Email: <input type='text' name='email' value='").append(inh.getEmail() != null ? inh.getEmail() : "").append("'/><br/>\n")
                .append("<button type='submit'>Save</button>\n");

        // Styled cancel button
        out.append("<a href='/sandhurst/house?houseId=").append(house.getId()).append("' style='margin-left:10px; padding:8px 16px; background:#ccc; color:black; text-decoration:none; border-radius:6px;'>Cancel</a>\n");

        out.append("</form><br/><br/>\n");

        // Drag-and-drop style upload form
        out.append("<form method='POST' action='/sandhurst/uploadImage' enctype='multipart/form-data'>\n")
                .append("<input type='hidden' name='inhabitantId' value='").append(inh.getId()).append("'/>\n")
                .append("<input type='hidden' name='sourceType' value='Inhabitant'/>\n")
                .append("<div class='dropzone' ondrop='handleDrop(event)' ondragover='handleDragOver(event)' ondragleave='handleDragLeave(event)'>\n")
                .append("Drop image here or click to select<br/><input type='file' name='file' accept='image/*' style='display:none' onchange='this.form.submit()'/>\n")
                .append("</div>\n")
                .append("</form>\n");

        // Image display and delete
        out.append("<h3>Images</h3>\n");
        for (ImageAsset image : inh.getImages()) {
            out.append("<div style='margin:10px 0;'>")
                    .append("<img src='").append(image.getImageUrl()).append("' width='200' /><br/>")
                    .append("<form method='POST' action='/sandhurst/deleteImage'>")
                    .append("<input type='hidden' name='imageId' value='").append(image.getId()).append("'/>")
                    .append("<button type='submit'>Delete</button>")
                    .append("</form>")
                    .append("</div>\n");
        }

        out.append("<script>\n")
                .append("function handleDrop(e) {\n")
                .append("  e.preventDefault(); const input = e.currentTarget.querySelector('input[type=file]'); input.files = e.dataTransfer.files; input.form.submit(); }\n")
                .append("function handleDragOver(e) { e.preventDefault(); e.currentTarget.classList.add('hover'); }\n")
                .append("function handleDragLeave(e) { e.currentTarget.classList.remove('hover'); }\n")
                .append("document.querySelector('.dropzone').addEventListener('click', function() {\n")
                .append("  this.querySelector('input[type=file]').click(); });\n")
                .append("</script>\n");

        out.append("</body></html>\n");

        return out.toString();
    }
}
