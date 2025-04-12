class ImageUploader {
    constructor(uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    async uploadImage(fileInputId) {
        // Asegurarse de que el elemento existe
        const fileInput = document.getElementById(fileInputId);
        if (!fileInput) {
            console.error(`No se encontró ningún elemento con el ID "${fileInputId}"`);
            return;
        }

        // Obtener el archivo
        const imageFile = fileInput.files[0];
        if (!imageFile) {
            console.error("No se seleccionó ningún archivo.");
            return;
        }

        // Crear FormData y enviar la imagen al servidor
        const formData = new FormData();
        formData.append("file", imageFile);
        formData.append("user", localStorage.getItem("userEmail"));
        console.log("Se va a enviar: la imagen" + imageFile.toString()+" el usuario: "+localStorage.getItem("userEmail")+ "el metofo: "+this.uploadUrl );
        try {
            const response = await fetch(this.uploadUrl, {
                method: "POST",
                body: formData,
            });
            if (!response.ok) {
                throw new Error("Error al subir la imagen: " + response.statusText);
            }
            const data = await response.json();
            console.log("Imagen subida exitosamente:", data);
            return data;
        } catch (error) {
            console.error("Error al subir la imagen:", error);
        }
    }
}
