/**
 * Customer Validation System
 * Handles validation for customer data
 * @author MINARI E-Commerce Team
 */

class EmailValidator {
    validate(email) {
        if (!email) {
            throw new Error("Email is required");
        }
        if (!this.isValidEmailFormat(email)) {
            throw new Error("Invalid email format");
        }
        return true;
    }

    isValidEmailFormat(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }
}

class PhoneValidator {
    validate(phone) {
        if (!phone) {
            return true; // Phone is optional
        }
        if (!this.isValidPhoneFormat(phone)) {
            throw new Error("Invalid phone format. Use format: 08XXXXXXXXX or +62XXXXXXXXX");
        }
        return true;
    }

    isValidPhoneFormat(phone) {
        // Accept Indonesian phone numbers
        const indonesianRegex = /^(\+62|0)[0-9]{9,12}$/;
        return indonesianRegex.test(phone.replace(/[^\d+]/g, ''));
    }
}

class FullNameValidator {
    validate(fullName) {
        if (!fullName) {
            throw new Error("Full name is required");
        }
        if (fullName.trim().length < 3) {
            throw new Error("Full name must be at least 3 characters");
        }
        if (fullName.trim().length > 100) {
            throw new Error("Full name must not exceed 100 characters");
        }
        return true;
    }
}

class PasswordValidator {
    validate(password) {
        if (!password) {
            throw new Error("Password is required");
        }
        if (password.length < 6) {
            throw new Error("Password must be at least 6 characters");
        }
        if (password.length > 50) {
            throw new Error("Password must not exceed 50 characters");
        }
        if (!this.hasStrongPassword(password)) {
            throw new Error("Password must contain uppercase, lowercase, number, and special character");
        }
        return true;
    }

    hasStrongPassword(password) {
        const strongRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;
        return strongRegex.test(password);
    }
}

class PasswordConfirmValidator {
    validate(password, confirmPassword) {
        if (!confirmPassword) {
            throw new Error("Password confirmation is required");
        }
        if (password !== confirmPassword) {
            throw new Error("Passwords do not match");
        }
        return true;
    }
}

class AddressValidator {
    validate(address) {
        if (!address) {
            return true; // Address is optional
        }
        
        if (!address.street || address.street.trim().length === 0) {
            throw new Error("Street address is required");
        }
        if (!address.city || address.city.trim().length === 0) {
            throw new Error("City is required");
        }
        if (!address.postalCode || address.postalCode.trim().length === 0) {
            throw new Error("Postal code is required");
        }
        if (!address.country || address.country.trim().length === 0) {
            throw new Error("Country is required");
        }
        
        return true;
    }
}

class LoyaltyPointsValidator {
    validate(points) {
        if (points === null || points === undefined) {
            throw new Error("Loyalty points is required");
        }
        if (!Number.isInteger(points)) {
            throw new Error("Loyalty points must be a number");
        }
        if (points < 0) {
            throw new Error("Loyalty points cannot be negative");
        }
        if (points > 999999) {
            throw new Error("Loyalty points cannot exceed 999,999");
        }
        return true;
    }
}

class NewsletterSubscriptionValidator {
    validate(subscribed) {
        if (typeof subscribed !== 'boolean') {
            throw new Error("Newsletter subscription must be a boolean value");
        }
        return true;
    }
}

class CustomerStatusValidator {
    validate(isActive) {
        if (typeof isActive !== 'boolean') {
            throw new Error("Customer status must be a boolean value");
        }
        return true;
    }
}

class CityValidator {
    validate(city) {
        if (!city) {
            throw new Error("City is required");
        }
        if (city.trim().length < 2) {
            throw new Error("City name must be at least 2 characters");
        }
        if (city.trim().length > 50) {
            throw new Error("City name must not exceed 50 characters");
        }
        return true;
    }
}

class CountryValidator {
    validate(country) {
        if (!country) {
            throw new Error("Country is required");
        }
        if (country.trim().length < 2) {
            throw new Error("Country name must be at least 2 characters");
        }
        if (country.trim().length > 50) {
            throw new Error("Country name must not exceed 50 characters");
        }
        return true;
    }
}

/**
 * Main Customer Validator
 * Orchestrates all customer-related validations
 */
class CustomerValidator {
    constructor() {
        this.emailValidator = new EmailValidator();
        this.phoneValidator = new PhoneValidator();
        this.nameValidator = new FullNameValidator();
        this.passwordValidator = new PasswordValidator();
        this.passwordConfirmValidator = new PasswordConfirmValidator();
        this.addressValidator = new AddressValidator();
        this.loyaltyPointsValidator = new LoyaltyPointsValidator();
        this.newsletterValidator = new NewsletterSubscriptionValidator();
        this.statusValidator = new CustomerStatusValidator();
        this.cityValidator = new CityValidator();
        this.countryValidator = new CountryValidator();
    }

    /**
     * Validate customer registration form
     */
    validateRegistration(data) {
        const errors = [];

        try {
            this.emailValidator.validate(data.email);
        } catch (e) {
            errors.push(e.message);
        }

        try {
            this.nameValidator.validate(data.fullName);
        } catch (e) {
            errors.push(e.message);
        }

        try {
            this.phoneValidator.validate(data.phone);
        } catch (e) {
            errors.push(e.message);
        }

        try {
            this.passwordValidator.validate(data.password);
        } catch (e) {
            errors.push(e.message);
        }

        try {
            this.passwordConfirmValidator.validate(data.password, data.confirmPassword);
        } catch (e) {
            errors.push(e.message);
        }

        return {
            isValid: errors.length === 0,
            errors
        };
    }

    /**
     * Validate customer profile update
     */
    validateProfileUpdate(data) {
        const errors = [];

        if (data.fullName) {
            try {
                this.nameValidator.validate(data.fullName);
            } catch (e) {
                errors.push(e.message);
            }
        }

        if (data.phone) {
            try {
                this.phoneValidator.validate(data.phone);
            } catch (e) {
                errors.push(e.message);
            }
        }

        if (data.shippingAddress) {
            try {
                this.addressValidator.validate(data.shippingAddress);
            } catch (e) {
                errors.push(e.message);
            }
        }

        return {
            isValid: errors.length === 0,
            errors
        };
    }

    /**
     * Validate loyalty points operation
     */
    validateLoyaltyPointsOperation(points) {
        const errors = [];

        try {
            this.loyaltyPointsValidator.validate(points);
        } catch (e) {
            errors.push(e.message);
        }

        return {
            isValid: errors.length === 0,
            errors
        };
    }

    /**
     * Validate email
     */
    validateEmail(email) {
        const errors = [];
        try {
            this.emailValidator.validate(email);
        } catch (e) {
            errors.push(e.message);
        }
        return {
            isValid: errors.length === 0,
            errors
        };
    }

    /**
     * Validate phone
     */
    validatePhone(phone) {
        const errors = [];
        try {
            this.phoneValidator.validate(phone);
        } catch (e) {
            errors.push(e.message);
        }
        return {
            isValid: errors.length === 0,
            errors
        };
    }

    /**
     * Validate city
     */
    validateCity(city) {
        const errors = [];
        try {
            this.cityValidator.validate(city);
        } catch (e) {
            errors.push(e.message);
        }
        return {
            isValid: errors.length === 0,
            errors
        };
    }

    /**
     * Validate country
     */
    validateCountry(country) {
        const errors = [];
        try {
            this.countryValidator.validate(country);
        } catch (e) {
            errors.push(e.message);
        }
        return {
            isValid: errors.length === 0,
            errors
        };
    }

    /**
     * Validate full customer data (admin)
     */
    validateCompleteCustomer(data) {
        const errors = [];

        try {
            this.emailValidator.validate(data.email);
        } catch (e) {
            errors.push(e.message);
        }

        try {
            this.nameValidator.validate(data.fullName);
        } catch (e) {
            errors.push(e.message);
        }

        if (data.phone) {
            try {
                this.phoneValidator.validate(data.phone);
            } catch (e) {
                errors.push(e.message);
            }
        }

        if (data.shippingAddress) {
            try {
                this.addressValidator.validate(data.shippingAddress);
            } catch (e) {
                errors.push(e.message);
            }
        }

        try {
            this.statusValidator.validate(data.isActive);
        } catch (e) {
            errors.push(e.message);
        }

        try {
            this.newsletterValidator.validate(data.newsletterSubscribed);
        } catch (e) {
            errors.push(e.message);
        }

        try {
            this.loyaltyPointsValidator.validate(data.loyaltyPoints);
        } catch (e) {
            errors.push(e.message);
        }

        return {
            isValid: errors.length === 0,
            errors
        };
    }
}

// Export for browser usage
if (typeof module !== 'undefined' && module.exports) {
    module.exports = CustomerValidator;
}
