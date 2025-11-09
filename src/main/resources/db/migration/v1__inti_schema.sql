-- V1__init_schema.sql
-- Initial database schema for Sales Tracking SaaS

CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- for gen_random_uuid()

-- =====================================================
-- USERS TABLE
-- =====================================================
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       email VARCHAR(150) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       shop_name VARCHAR(150) NOT NULL,
                        refreshToken VARCHAR(250) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP,
                       deleted_at TIMESTAMP
);

-- =====================================================
-- CUSTOMERS TABLE
-- =====================================================
CREATE TABLE customers (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           name VARCHAR(150) NOT NULL,
                           phone VARCHAR(50),
                           email VARCHAR(150),
                           total_spent NUMERIC(12,2) DEFAULT 0,
                           user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP,
                           deleted_at TIMESTAMP
);

-- =====================================================
-- PRODUCTS TABLE
-- =====================================================
CREATE TABLE products (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name VARCHAR(150) NOT NULL,
                          price NUMERIC(12,2) NOT NULL,
                          total_stock INT NOT NULL,
                          sold_items INT DEFAULT 0,
                          remaining_stock INT GENERATED ALWAYS AS (total_stock - sold_items) STORED,
                          user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP,
                          deleted_at TIMESTAMP
);

-- =====================================================
-- ORDERS TABLE
-- =====================================================
CREATE TABLE orders (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        customer_id UUID REFERENCES customers(id) ON DELETE CASCADE,
                        user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        status VARCHAR(50) DEFAULT 'PENDING',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP,
                        deleted_at TIMESTAMP
);

-- =====================================================
-- ORDER ITEMS TABLE
-- =====================================================
CREATE TABLE order_items (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
                             product_id UUID REFERENCES products(id) ON DELETE CASCADE,
                             quantity INT NOT NULL,
                             price_at_sale NUMERIC(12,2) NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP,
                             deleted_at TIMESTAMP
);

-- =====================================================
-- PAYMENTS TABLE
-- =====================================================
CREATE TABLE payments (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
                          amount NUMERIC(12,2) NOT NULL,
                          payment_method VARCHAR(50),
                          payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          status VARCHAR(50) DEFAULT 'UNPAID',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP,
                          deleted_at TIMESTAMP
);
