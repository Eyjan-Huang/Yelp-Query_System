CREATE TABLE Users (
    uid VARCHAR(100) NOT NULL,
    user_name VARCHAR(50) NOT NULL,
    date_created DATE NOT NULL,
    review_count INT DEFAULT 0,
    num_of_friends INT DEFAULT 0,
    avg_stars FLOAT(3, 2),
    vote_funny INT DEFAULT 0,
    vote_useful INT DEFAULT 0,
    vote_cool INT DEFAULT 0,
    total_votes INT DEFAULT 0,
    PRIMARY KEY (uid),
    INDEX users_indexes (date_created, review_count, avg_stars)
);


CREATE TABLE Business (
    bid VARCHAR(100) NOT NULL,
    full_address VARCHAR(250),
    city VARCHAR(50),
    review_count INT DEFAULT 0,
    business_name VARCHAR(150),
    latitude FLOAT(6, 3),
    longitude FLOAT(6, 3),
    state CHAR(2),
    stars FLOAT(3, 2),
    is_open BOOLEAN,
    PRIMARY KEY (bid),
    INDEX business_stars_idx (stars),
    INDEX business_state_idx (state)
);


CREATE TABLE Business_Category_Relation (
    bid VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    FOREIGN KEY (bid) REFERENCES Business(bid) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX category_idx (category)
);


CREATE TABLE Business_Subcategory_Relation (
    bid VARCHAR(100) NOT NULL,
    subcategory VARCHAR(50),
    FOREIGN KEY (bid) REFERENCES Business(bid) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX subcategory_idx (subcategory)
);


CREATE TABLE Reviews_Buffer (
    rid VARCHAR(100) NOT NULL,
    uid VARCHAR(100) NOT NULL,
    bid VARCHAR(100) NOT NULL,
    review_date DATE,
    stars INT,
    review_text TEXT(255),
    useful_votes INT,
    cool_votes INT,
    funny_votes INT,
    PRIMARY KEY (rid),
    FOREIGN KEY (uid) REFERENCES Users(uid) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (bid) REFERENCES Business(bid) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE Review (
    rid VARCHAR(100) NOT NULL,
    uid VARCHAR(100) NOT NULL,
    bid VARCHAR(100) NOT NULL,
    review_date DATE,
    stars INT,
    review_text TEXT(255),
    business_name VARCHAR(150),
    useful_votes INT,
    cool_votes INT,
    funny_votes INT,
    total_votes INT,
    PRIMARY KEY (rid),
    FOREIGN KEY (uid) REFERENCES Users(uid) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (bid) REFERENCES Business(bid) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX reviews_date_idx (review_date),
    INDEX reviews_stars_idx (stars),
    INDEX reviews_votes_idx (useful_votes, cool_votes, funny_votes)
);


CREATE TABLE Checkin (
    bid VARCHAR(100) NOT NULL,
    info JSON,
    FOREIGN KEY (bid) REFERENCES Business(bid) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE Business_Attributes_Relation (
    bid VARCHAR(100) NOT NULL,
    has_tv BOOLEAN DEFAULT NULL,
    coat_check BOOLEAN DEFAULT NULL,
    open_24_hour BOOLEAN DEFAULT NULL,
    accept_insurance BOOLEAN DEFAULT NULL,
    alcohol VARCHAR(20) CHECK (alcohol IN ('full_bar', 'beer_and_wine', 'none')) DEFAULT NULL,
    dogs_allowed BOOLEAN DEFAULT NULL,
    caters BOOLEAN DEFAULT NULL,
    price_range TINYINT CHECK (price_range BETWEEN 1 AND 4) DEFAULT NULL,
    happy_hour BOOLEAN DEFAULT NULL,
    good_for_kid BOOLEAN DEFAULT NULL,
    good_for_dancing BOOLEAN DEFAULT NULL,
    outdoor_seating BOOLEAN DEFAULT NULL,
    take_reservation BOOLEAN DEFAULT NULL,
    waiter_service BOOLEAN DEFAULT NULL,
    wifi VARCHAR(5) CHECK (wifi IN ('no', 'paid', 'free')) DEFAULT NULL,
    good_for VARCHAR(255) CHARACTER SET utf8,
    parking VARCHAR(255) CHARACTER SET utf8,
    hair_types_specialized VARCHAR(255) CHARACTER SET utf8,
    drive_thru BOOLEAN DEFAULT NULL,
    order_at_counter BOOLEAN DEFAULT NULL,
    accept_credit_cards BOOLEAN DEFAULT NULL,
    BYOB_corkage VARCHAR(15) CHECK (BYOB_corkage IN ('no', 'yes_corkage', 'yes_free')) DEFAULT NULL,
    good_for_groups BOOLEAN DEFAULT NULL,
    noise_level VARCHAR(15) CHECK (noise_level IN ('loud', 'average', 'quiet', 'very_loud')) DEFAULT NULL,
    by_appointment BOOLEAN DEFAULT NULL,
    take_out BOOLEAN DEFAULT NULL,
    wheelchair_accessible BOOLEAN DEFAULT NULL,
    BYOB BOOLEAN DEFAULT NULL,
    music VARCHAR(255) CHARACTER SET utf8,
    attire VARCHAR(10) CHECK (attire IN ('formal', 'casual', 'dressy')) DEFAULT NULL,
    payment_types VARCHAR(255) CHARACTER SET utf8,
    delivery BOOLEAN DEFAULT NULL,
    ambience VARCHAR(255) CHARACTER SET utf8,
    dietary_restrictions VARCHAR(255) CHARACTER SET utf8,
    corkage BOOLEAN DEFAULT NULL,
    ages_allowed VARCHAR(10) CHECK (ages_allowed IN ('21plus', 'allages', '18plus')) DEFAULT NULL,
    smoking VARCHAR(10) CHECK (smoking IN ('yes', 'no', 'outdoor')) DEFAULT NULL,
    FOREIGN KEY (bid) REFERENCES Business(bid) ON DELETE CASCADE ON UPDATE CASCADE
);
