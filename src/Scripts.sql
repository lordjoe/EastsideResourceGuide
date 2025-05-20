-- 1. Main Resources Table
CREATE TABLE community_resources (
                                     id SERIAL PRIMARY KEY,
                                     category VARCHAR(255),
                                     subcategory VARCHAR(255),
                                     name VARCHAR(500) NOT NULL,
                                     description TEXT,
                                     address_line1 VARCHAR(500),
                                     address_line2 VARCHAR(500),
                                     city VARCHAR(255),
                                     state VARCHAR(50),
                                     zip_code VARCHAR(20),
                                     phone_primary VARCHAR(50),
                                     phone_secondary VARCHAR(50),
                                     email VARCHAR(255),
                                     hours TEXT,
                                     notes TEXT
);

-- 2. Resource Properties Table (name-value pairs)
CREATE TABLE community_resource_properties (
                                               id SERIAL PRIMARY KEY,
                                               resource_id INTEGER NOT NULL REFERENCES community_resources(id) ON DELETE CASCADE,
                                               property_name VARCHAR(255) NOT NULL,
                                               property_value TEXT
);

-- 3. Resource Sections Table (optional: maps sections to expected properties)
CREATE TABLE community_resource_sections (
                                             id SERIAL PRIMARY KEY,
                                             section_name VARCHAR(255) NOT NULL,
                                             property_name VARCHAR(255) NOT NULL
);

-- 4. URLs Table with Validation Info
CREATE TABLE resource_urls (
                               id SERIAL PRIMARY KEY,
                               resource_id INTEGER NOT NULL REFERENCES community_resources(id) ON DELETE CASCADE,
                               url_type VARCHAR(255),
                               url TEXT NOT NULL,
                               is_validated BOOLEAN DEFAULT FALSE,
                               is_valid BOOLEAN,
                               last_checked TIMESTAMP,
                               notes TEXT
);
