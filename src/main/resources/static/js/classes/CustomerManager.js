/**
 * Customer Management System
 * Handles all customer CRUD operations and interactions
 * @author MINARI E-Commerce Team
 */

class CustomerManager {
    constructor(apiBaseUrl = '/api/customers') {
        this.apiBaseUrl = apiBaseUrl;
        this.validator = new CustomerValidator();
        this.currentPage = 0;
        this.pageSize = 10;
    }

    /**
     * ===== CRUD OPERATIONS =====
     */

    /**
     * Get all customers with pagination
     */
    async getAllCustomers(page = 0, size = 10) {
        try {
            const response = await fetch(
                `${this.apiBaseUrl}?page=${page}&size=${size}`,
                {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                this.currentPage = page;
                this.pageSize = size;
                return {
                    success: true,
                    customers: data.data,
                    totalItems: data.totalItems,
                    totalPages: data.totalPages,
                    currentPage: page
                };
            } else {
                throw new Error(data.error || 'Failed to fetch customers');
            }
        } catch (error) {
            console.error('Error fetching customers:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Get single customer by ID
     */
    async getCustomerById(customerId) {
        try {
            const response = await fetch(
                `${this.apiBaseUrl}/${customerId}`,
                {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    customer: data.data
                };
            } else {
                throw new Error(data.error || 'Failed to fetch customer');
            }
        } catch (error) {
            console.error('Error fetching customer:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Search customers
     */
    async searchCustomers(keyword, page = 0, size = 10) {
        try {
            const response = await fetch(
                `${this.apiBaseUrl}/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`,
                {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    customers: data.data,
                    totalItems: data.totalItems,
                    keyword: keyword
                };
            } else {
                throw new Error(data.error || 'Failed to search customers');
            }
        } catch (error) {
            console.error('Error searching customers:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Create new customer
     */
    async createCustomer(customerData) {
        try {
            // Validate data
            const validation = this.validator.validateCompleteCustomer(customerData);
            if (!validation.isValid) {
                return {
                    success: false,
                    errors: validation.errors
                };
            }

            const response = await fetch(
                `${this.apiBaseUrl}`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(customerData)
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    message: data.message,
                    customer: data.data
                };
            } else {
                throw new Error(data.error || 'Failed to create customer');
            }
        } catch (error) {
            console.error('Error creating customer:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Update customer
     */
    async updateCustomer(customerId, customerData) {
        try {
            // Validate data
            const validation = this.validator.validateProfileUpdate(customerData);
            if (!validation.isValid) {
                return {
                    success: false,
                    errors: validation.errors
                };
            }

            const response = await fetch(
                `${this.apiBaseUrl}/${customerId}`,
                {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(customerData)
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    message: data.message,
                    customer: data.data
                };
            } else {
                throw new Error(data.error || 'Failed to update customer');
            }
        } catch (error) {
            console.error('Error updating customer:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Delete customer
     */
    async deleteCustomer(customerId) {
        try {
            const response = await fetch(
                `${this.apiBaseUrl}/${customerId}`,
                {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    message: data.message
                };
            } else {
                throw new Error(data.error || 'Failed to delete customer');
            }
        } catch (error) {
            console.error('Error deleting customer:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * ===== STATUS OPERATIONS =====
     */

    /**
     * Toggle customer status (active/inactive)
     */
    async toggleCustomerStatus(customerId) {
        try {
            const response = await fetch(
                `${this.apiBaseUrl}/${customerId}/status`,
                {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    message: data.message,
                    customer: data.data
                };
            } else {
                throw new Error(data.error || 'Failed to toggle customer status');
            }
        } catch (error) {
            console.error('Error toggling customer status:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * ===== NEWSLETTER OPERATIONS =====
     */

    /**
     * Toggle newsletter subscription
     */
    async toggleNewsletterSubscription(customerId) {
        try {
            const response = await fetch(
                `${this.apiBaseUrl}/${customerId}/newsletter`,
                {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    message: data.message,
                    customer: data.data
                };
            } else {
                throw new Error(data.error || 'Failed to toggle newsletter subscription');
            }
        } catch (error) {
            console.error('Error toggling newsletter subscription:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Get newsletter subscribers
     */
    async getNewsletterSubscribers() {
        try {
            const response = await fetch(
                `${this.apiBaseUrl}/newsletter-subscribers`,
                {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    subscribers: data.data,
                    count: data.count
                };
            } else {
                throw new Error(data.error || 'Failed to fetch newsletter subscribers');
            }
        } catch (error) {
            console.error('Error fetching newsletter subscribers:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * ===== LOYALTY POINTS OPERATIONS =====
     */

    /**
     * Add loyalty points to customer
     */
    async addLoyaltyPoints(customerId, points) {
        try {
            // Validate points
            const validation = this.validator.validateLoyaltyPointsOperation(points);
            if (!validation.isValid) {
                return {
                    success: false,
                    errors: validation.errors
                };
            }

            const response = await fetch(
                `${this.apiBaseUrl}/${customerId}/loyalty-points/add?points=${points}`,
                {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    message: data.message,
                    customer: data.data
                };
            } else {
                throw new Error(data.error || 'Failed to add loyalty points');
            }
        } catch (error) {
            console.error('Error adding loyalty points:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Deduct loyalty points from customer
     */
    async deductLoyaltyPoints(customerId, points) {
        try {
            // Validate points
            const validation = this.validator.validateLoyaltyPointsOperation(points);
            if (!validation.isValid) {
                return {
                    success: false,
                    errors: validation.errors
                };
            }

            const response = await fetch(
                `${this.apiBaseUrl}/${customerId}/loyalty-points/deduct?points=${points}`,
                {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    message: data.message,
                    customer: data.data
                };
            } else {
                throw new Error(data.error || 'Failed to deduct loyalty points');
            }
        } catch (error) {
            console.error('Error deducting loyalty points:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * ===== FILTERING & STATISTICS =====
     */

    /**
     * Get active customers
     */
    async getActiveCustomers(page = 0, size = 10) {
        try {
            const response = await fetch(
                `${this.apiBaseUrl}/active?page=${page}&size=${size}`,
                {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    customers: data.data,
                    totalItems: data.totalItems
                };
            } else {
                throw new Error(data.error || 'Failed to fetch active customers');
            }
        } catch (error) {
            console.error('Error fetching active customers:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Get inactive customers
     */
    async getInactiveCustomers(page = 0, size = 10) {
        try {
            const response = await fetch(
                `${this.apiBaseUrl}/inactive?page=${page}&size=${size}`,
                {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    customers: data.data,
                    totalItems: data.totalItems
                };
            } else {
                throw new Error(data.error || 'Failed to fetch inactive customers');
            }
        } catch (error) {
            console.error('Error fetching inactive customers:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Get VIP customers
     */
    async getVIPCustomers(minPoints = 1000, page = 0, size = 10) {
        try {
            const response = await fetch(
                `${this.apiBaseUrl}/vip?minPoints=${minPoints}&page=${page}&size=${size}`,
                {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    customers: data.data,
                    totalItems: data.totalItems,
                    minPoints: minPoints
                };
            } else {
                throw new Error(data.error || 'Failed to fetch VIP customers');
            }
        } catch (error) {
            console.error('Error fetching VIP customers:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Get customer statistics
     */
    async getCustomerStatistics() {
        try {
            const response = await fetch(
                `${this.apiBaseUrl}/stats/summary`,
                {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    stats: data.data
                };
            } else {
                throw new Error(data.error || 'Failed to fetch customer statistics');
            }
        } catch (error) {
            console.error('Error fetching customer statistics:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Get customers by city
     */
    async getCustomersByCity(city) {
        try {
            const response = await fetch(
                `${this.apiBaseUrl}/by-city/${encodeURIComponent(city)}`,
                {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    customers: data.data,
                    city: city,
                    count: data.count
                };
            } else {
                throw new Error(data.error || 'Failed to fetch customers by city');
            }
        } catch (error) {
            console.error('Error fetching customers by city:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Get customers by country
     */
    async getCustomersByCountry(country) {
        try {
            const response = await fetch(
                `${this.apiBaseUrl}/by-country/${encodeURIComponent(country)}`,
                {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            if (data.success) {
                return {
                    success: true,
                    customers: data.data,
                    country: country,
                    count: data.count
                };
            } else {
                throw new Error(data.error || 'Failed to fetch customers by country');
            }
        } catch (error) {
            console.error('Error fetching customers by country:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * ===== UTILITY METHODS =====
     */

    /**
     * Export customers to JSON
     */
    async exportToJSON(customers) {
        const dataStr = JSON.stringify(customers, null, 2);
        const dataBlob = new Blob([dataStr], { type: 'application/json' });
        this.downloadFile(dataBlob, 'customers.json');
    }

    /**
     * Export customers to CSV
     */
    async exportToCSV(customers) {
        if (customers.length === 0) {
            alert('No customers to export');
            return;
        }

        const headers = ['ID', 'Email', 'Full Name', 'Phone', 'City', 'Country', 'Active', 'Loyalty Points'];
        const csvContent = [
            headers.join(','),
            ...customers.map(c => [
                c.id,
                c.email,
                c.fullName,
                c.phone || '',
                c.shippingAddress?.city || '',
                c.shippingAddress?.country || '',
                c.isActive ? 'Yes' : 'No',
                c.loyaltyPoints || 0
            ].join(','))
        ].join('\n');

        const dataBlob = new Blob([csvContent], { type: 'text/csv' });
        this.downloadFile(dataBlob, 'customers.csv');
    }

    /**
     * Download file helper
     */
    downloadFile(blob, filename) {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
    }
}

// Export for browser usage
if (typeof module !== 'undefined' && module.exports) {
    module.exports = CustomerManager;
}
