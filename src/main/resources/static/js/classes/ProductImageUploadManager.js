/**
 * Product Image Upload Manager
 * 
 * Handle multiple image uploads dengan preview, validation, dan management
 * Features: Preview, drag-drop, remove, reorder, compression
 * 
 * Demonstrates: Encapsulation, DOM manipulation, File API, Event handling
 */

class ProductImageUploadManager {
    constructor(containerId, options = {}) {
        this.container = document.getElementById(containerId);
        this.maxImages = options.maxImages || 5;
        this.maxFileSize = options.maxFileSize || 5 * 1024 * 1024; // 5MB
        this.allowedTypes = options.allowedTypes || ['image/jpeg', 'image/png', 'image/gif'];
        
        this.images = []; // Array to store image data
        this.uploadedFiles = new Map(); // Map to track files by ID
        
        this.cacheElements();
        this.setupEventListeners();
    }

    /**
     * Cache DOM elements
     * @private
     */
    cacheElements() {
        this.uploadArea = this.container.querySelector('.upload-area');
        this.fileInput = this.container.querySelector('.file-input');
        this.previewContainer = this.container.querySelector('.image-preview-container');
        this.imageCount = this.container.querySelector('.image-count');
    }

    /**
     * Setup event listeners
     * @private
     */
    setupEventListeners() {
        // File input change
        this.fileInput.addEventListener('change', (e) => this.handleFileSelect(e));

        // Drag and drop
        this.uploadArea.addEventListener('dragover', (e) => this.handleDragOver(e));
        this.uploadArea.addEventListener('dragleave', (e) => this.handleDragLeave(e));
        this.uploadArea.addEventListener('drop', (e) => this.handleDrop(e));

        // Click to select files
        this.uploadArea.addEventListener('click', () => this.fileInput.click());
    }

    /**
     * Handle file selection from input
     * @private
     */
    handleFileSelect(event) {
        const files = event.target.files;
        this.processFiles(Array.from(files));
    }

    /**
     * Handle drag over
     * @private
     */
    handleDragOver(event) {
        event.preventDefault();
        event.stopPropagation();
        this.uploadArea.classList.add('dragover');
    }

    /**
     * Handle drag leave
     * @private
     */
    handleDragLeave(event) {
        event.preventDefault();
        event.stopPropagation();
        this.uploadArea.classList.remove('dragover');
    }

    /**
     * Handle file drop
     * @private
     */
    handleDrop(event) {
        event.preventDefault();
        event.stopPropagation();
        this.uploadArea.classList.remove('dragover');

        const files = event.dataTransfer.files;
        this.processFiles(Array.from(files));
    }

    /**
     * Process selected/dropped files
     * @private
     */
    processFiles(files) {
        const validFiles = [];

        for (const file of files) {
            // Check image count limit
            if (this.images.length >= this.maxImages) {
                this.showError(`Maximum ${this.maxImages} images allowed`);
                break;
            }

            // Validate file
            if (this.validateFile(file)) {
                validFiles.push(file);
            }
        }

        // Create previews for valid files
        validFiles.forEach(file => {
            this.createImagePreview(file);
        });
    }

    /**
     * Validate file format and size
     * @private
     */
    validateFile(file) {
        // Check file type
        if (!this.allowedTypes.includes(file.type)) {
            this.showError(`Invalid file type: ${file.name}. Allowed: JPG, PNG, GIF`);
            return false;
        }

        // Check file size
        if (file.size > this.maxFileSize) {
            const sizeMB = (file.size / (1024 * 1024)).toFixed(2);
            this.showError(`File too large: ${file.name} (${sizeMB}MB). Max: 5MB`);
            return false;
        }

        return true;
    }

    /**
     * Create image preview
     * @private
     */
    createImagePreview(file) {
        const reader = new FileReader();
        const imageId = this.generateId();

        reader.onload = (event) => {
            // Store image data
            const imageData = {
                id: imageId,
                file: file,
                dataUrl: event.target.result,
                size: this.formatFileSize(file.size),
                name: file.name,
                isPrimary: this.images.length === 0 // First image is primary
            };

            this.images.push(imageData);
            this.uploadedFiles.set(imageId, imageData);

            // Render preview
            this.renderImagePreview(imageData);
            this.updateImageCount();
            this.updateUploadArea();
        };

        reader.readAsDataURL(file);
    }

    /**
     * Render single image preview
     * @private
     */
    renderImagePreview(imageData) {
        const previewItem = document.createElement('div');
        previewItem.className = 'image-preview-item';
        previewItem.id = `preview-${imageData.id}`;
        previewItem.innerHTML = `
            <div class="preview-image">
                <img src="${imageData.dataUrl}" alt="Preview">
                ${imageData.isPrimary ? '<span class="primary-badge">Primary</span>' : ''}
            </div>
            <div class="preview-info">
                <p class="file-name">${imageData.name}</p>
                <p class="file-size">${imageData.size}</p>
            </div>
            <div class="preview-actions">
                <button type="button" class="btn-small btn-primary btn-set-primary" 
                        ${imageData.isPrimary ? 'disabled' : ''}>
                    Set Primary
                </button>
                <button type="button" class="btn-small btn-danger btn-remove">
                    Remove
                </button>
            </div>
        `;

        // Setup action buttons
        const setPrimaryBtn = previewItem.querySelector('.btn-set-primary');
        const removeBtn = previewItem.querySelector('.btn-remove');

        if (setPrimaryBtn && !setPrimaryBtn.disabled) {
            setPrimaryBtn.addEventListener('click', () => this.setPrimaryImage(imageData.id));
        }

        removeBtn.addEventListener('click', () => this.removeImage(imageData.id));

        this.previewContainer.appendChild(previewItem);
    }

    /**
     * Set image as primary
     */
    setPrimaryImage(imageId) {
        // Reset all primary flags
        this.images.forEach(img => img.isPrimary = false);

        // Set selected as primary
        const selectedImage = this.uploadedFiles.get(imageId);
        if (selectedImage) {
            selectedImage.isPrimary = true;
        }

        // Re-render previews
        this.renderAllPreviews();
    }

    /**
     * Remove image
     */
    removeImage(imageId) {
        // Remove from images array
        this.images = this.images.filter(img => img.id !== imageId);

        // Remove from map
        this.uploadedFiles.delete(imageId);

        // Remove from DOM
        const previewElement = document.getElementById(`preview-${imageId}`);
        if (previewElement) {
            previewElement.remove();
        }

        // If no primary image, set first as primary
        if (this.images.length > 0 && !this.images.some(img => img.isPrimary)) {
            this.images[0].isPrimary = true;
        }

        this.updateImageCount();
        this.updateUploadArea();

        if (this.images.length === 0) {
            this.renderAllPreviews();
        }
    }

    /**
     * Re-render all previews
     * @private
     */
    renderAllPreviews() {
        this.previewContainer.innerHTML = '';
        this.images.forEach(imageData => {
            this.renderImagePreview(imageData);
        });
    }

    /**
     * Update image count display
     * @private
     */
    updateImageCount() {
        if (this.imageCount) {
            const remaining = this.maxImages - this.images.length;
            this.imageCount.textContent = `${this.images.length}/${this.maxImages}`;
            
            if (remaining === 0) {
                this.uploadArea.classList.add('disabled');
            } else {
                this.uploadArea.classList.remove('disabled');
            }
        }
    }

    /**
     * Update upload area appearance
     * @private
     */
    updateUploadArea() {
        if (this.images.length > 0) {
            this.uploadArea.classList.add('has-images');
        } else {
            this.uploadArea.classList.remove('has-images');
        }
    }

    /**
     * Get all selected images
     * @returns {Array} Array of image data
     */
    getImages() {
        return [...this.images];
    }

    /**
     * Get files for upload
     * @returns {File[]} Array of File objects
     */
    getFiles() {
        return this.images.map(img => img.file);
    }

    /**
     * Get primary image
     * @returns {Object|null} Primary image data or null
     */
    getPrimaryImage() {
        return this.images.find(img => img.isPrimary) || null;
    }

    /**
     * Validate images are selected
     * @returns {boolean} True if at least one image selected
     */
    hasImages() {
        return this.images.length > 0;
    }

    /**
     * Get image count
     * @returns {number} Number of selected images
     */
    getImageCount() {
        return this.images.length;
    }

    /**
     * Clear all images
     */
    clearImages() {
        this.images = [];
        this.uploadedFiles.clear();
        this.previewContainer.innerHTML = '';
        this.fileInput.value = '';
        this.updateImageCount();
        this.updateUploadArea();
    }

    /**
     * Show error message
     * @private
     */
    showError(message) {
        // Create notification
        const notification = document.createElement('div');
        notification.className = 'notification notification-error';
        notification.textContent = message;
        
        document.body.appendChild(notification);

        // Remove after 5 seconds
        setTimeout(() => {
            notification.remove();
        }, 5000);
    }

    /**
     * Format file size for display
     * @private
     */
    formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        
        return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
    }

    /**
     * Generate unique ID
     * @private
     */
    generateId() {
        return 'img_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }

    /**
     * Validate all images are valid
     * @returns {boolean}
     */
    validate() {
        if (this.images.length === 0) {
            this.showError('Please select at least one image');
            return false;
        }

        if (!this.images.some(img => img.isPrimary)) {
            this.showError('Please set a primary image');
            return false;
        }

        return true;
    }
}
