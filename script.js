class FoodReviewApp {
    constructor() {
        this.reviewsList = document.getElementById('reviewsList');
        this.statsDiv = document.getElementById('stats');
        this.addForm = document.getElementById('addForm');
        this.init();
    }

    init() {
        this.addForm.addEventListener('submit', (e) => this.handleAdd(e));
        this.loadData();
        this.loadStats();
    }

    async apiCall(endpoint, options = {}) {
        try {
            const response = await fetch(`/api${endpoint}`, options);
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            this.showMessage(`Error: ${error.message}`, 'error');
        }
    }

    async loadData() {
        this.reviewsList.innerHTML = '<div class="loading">Loading reviews...</div>';
        const reviews = await this.apiCall('/reviews');
        if (reviews && reviews.length > 0) {
            this.renderReviews(reviews);
        } else {
            this.reviewsList.innerHTML = '<div>No reviews added yet.</div>';
        }
    }

    async loadStats() {
        const stats = await this.apiCall('/stats');
        if (stats) {
            this.statsDiv.innerHTML = `
                Average Rating: ${stats.avgRating ? stats.avgRating.toFixed(1) : 'N/A'} ⭐<br>
                Highest Rated: ${stats.highestRated || 'None yet'}
            `;
        }
    }

    async handleAdd(e) {
        e.preventDefault();
        const formData = new FormData(this.addForm);
        const data = {
            type: document.getElementById('foodType').value,
            name: document.getElementById('foodName').value,
            rating: parseInt(document.getElementById('rating').value),
            review: document.getElementById('review').value
        };

        const response = await this.apiCall('/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response && response.success) {
            this.showMessage('Review added successfully!', 'success');
            this.addForm.reset();
            this.loadData();
            this.loadStats();
        }
    }

    renderReviews(reviews) {
        this.reviewsList.innerHTML = reviews.map(review => `
            <div class="review-card">
                <div class="review-header">
                    <span class="food-type">${review.name}</span>
                    <div class="rating-stars">⭐${'★'.repeat(review.rating-1)}${'☆'.repeat(5-review.rating)}</div>
                </div>
                <p>${review.review}</p>
            </div>
        `).join('');
    }

    showMessage(msg, type) {
        const msgDiv = document.createElement('div');
        msgDiv.className = type;
        msgDiv.textContent = msg;
        document.querySelector('.form-section').appendChild(msgDiv);
        setTimeout(() => msgDiv.remove(), 3000);
    }
}

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    new FoodReviewApp();
});
