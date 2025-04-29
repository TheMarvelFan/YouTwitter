USE Mthree;
SHOW TABLES;

-- CREATE TABLE Users (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     username VARCHAR(255) UNIQUE NOT NULL,
--     email VARCHAR(255) UNIQUE NOT NULL,
--     fullName VARCHAR(255) NOT NULL,
--     avatar VARCHAR(255) NOT NULL,
--     coverImage VARCHAR(255),
--     password VARCHAR(255) NOT NULL,
--     refreshToken VARCHAR(255),
--     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );

-- CREATE TABLE Videos (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     videoFile VARCHAR(255) NOT NULL,
--     thumbnail VARCHAR(255) NOT NULL,
--     title VARCHAR(255) NOT NULL,
--     description TEXT NOT NULL,
--     duration INT NOT NULL,
--     views INT DEFAULT 0,
--     isPublished BOOLEAN DEFAULT TRUE,
--     owner INT,
--     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     FOREIGN KEY (owner) REFERENCES Users(id) ON DELETE SET NULL
-- );

-- CREATE TABLE Comments (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     content TEXT NOT NULL,
--     video INT NOT NULL,
--     owner INT NOT NULL,
--     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     FOREIGN KEY (video) REFERENCES Videos(id) ON DELETE CASCADE,
--     FOREIGN KEY (owner) REFERENCES Users(id) ON DELETE CASCADE
-- );

-- CREATE TABLE Tweets (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     content TEXT NOT NULL,
--     owner INT,
--     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     FOREIGN KEY (owner) REFERENCES Users(id) ON DELETE SET NULL
-- );

-- CREATE TABLE Likes (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     video INT,
--     comment INT,
--     tweet INT,
--     likedBy INT,
--     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     FOREIGN KEY (video) REFERENCES Videos(id) ON DELETE SET NULL,
--     FOREIGN KEY (comment) REFERENCES Comments(id) ON DELETE SET NULL,
--     FOREIGN KEY (tweet) REFERENCES Tweets(id) ON DELETE SET NULL,
--     FOREIGN KEY (likedBy) REFERENCES Users(id) ON DELETE SET NULL
-- );

-- CREATE TABLE Playlists (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     name VARCHAR(255) NOT NULL,
--     description TEXT NOT NULL,
--     owner INT NOT NULL,
--     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     FOREIGN KEY (owner) REFERENCES Users(id) ON DELETE CASCADE
-- );

-- CREATE TABLE PlaylistVideos (
--     playlistId INT NOT NULL,
--     videoId INT NOT NULL,
--     PRIMARY KEY (playlistId, videoId),
--     FOREIGN KEY (playlistId) REFERENCES Playlists(id) ON DELETE CASCADE,
--     FOREIGN KEY (videoId) REFERENCES Videos(id) ON DELETE CASCADE
-- );

-- CREATE TABLE Subscriptions (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     subscriber INT NOT NULL,
--     channel INT NOT NULL,
--     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     FOREIGN KEY (subscriber) REFERENCES Users(id) ON DELETE CASCADE,
--     FOREIGN KEY (channel) REFERENCES Users(id) ON DELETE CASCADE
-- );

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    avatar VARCHAR(255),
    cover_image VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- Create videos table
CREATE TABLE videos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    video_file VARCHAR(255) NOT NULL,
    thumbnail VARCHAR(255),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration BIGINT NOT NULL,
    views INT DEFAULT 0,
    is_published BOOLEAN DEFAULT FALSE,
    owner BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_owner (owner),
    INDEX idx_title (title),
    INDEX idx_is_published (is_published)
);

-- Create comments table
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL,
    video BIGINT,
    owner BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (video) REFERENCES videos(id) ON DELETE CASCADE,
    FOREIGN KEY (owner) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_video (video),
    INDEX idx_owner (owner)
);

-- Create tweets table
CREATE TABLE tweets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL,
    owner BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_owner (owner)
);

-- Create playlists table
CREATE TABLE playlists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    owner BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_owner (owner)
);

-- Create playlistvideos junction table
CREATE TABLE playlistvideos (
    playlist_id BIGINT,
    video_id BIGINT,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (playlist_id, video_id),
    FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE,
    FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE
);

-- Create subscriptions table
CREATE TABLE subscriptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    subscriber BIGINT,
    channel BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (subscriber) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (channel) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_subscription (subscriber, channel),
    INDEX idx_subscriber (subscriber),
    INDEX idx_channel (channel)
);

CREATE TABLE likes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    likeable_type ENUM('video', 'comment', 'tweet') NOT NULL,
    likeable_id BIGINT NOT NULL,
    liked_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (liked_by) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_like (likeable_type, likeable_id, liked_by),
    INDEX idx_likeable (likeable_type, likeable_id),
    INDEX idx_liked_by (liked_by)
);

-- Step 1: Drop all foreign key constraints to allow modifying referenced columns
-- Note the order: We need to drop the constraints before modifying the columns they reference

-- Drop foreign keys from Likes table
ALTER TABLE Likes
DROP FOREIGN KEY likes_ibfk_1, -- video reference
DROP FOREIGN KEY likes_ibfk_2, -- comment reference
DROP FOREIGN KEY likes_ibfk_3, -- tweet reference
DROP FOREIGN KEY likes_ibfk_4; -- likedBy reference

-- Drop foreign keys from Comments table
ALTER TABLE Comments
DROP FOREIGN KEY comments_ibfk_1, -- video reference
DROP FOREIGN KEY comments_ibfk_2; -- owner reference

-- Drop foreign keys from Videos table
ALTER TABLE Videos
DROP FOREIGN KEY videos_ibfk_1; -- owner reference

-- Drop foreign keys from Tweets table
ALTER TABLE Tweets
DROP FOREIGN KEY tweets_ibfk_1; -- owner reference

-- Drop foreign keys from Playlists table
ALTER TABLE Playlists
DROP FOREIGN KEY playlists_ibfk_1; -- owner reference

-- Drop foreign keys from PlaylistVideos table
ALTER TABLE PlaylistVideos
DROP FOREIGN KEY playlistvideos_ibfk_1, -- playlistId reference
DROP FOREIGN KEY playlistvideos_ibfk_2; -- videoId reference

-- Drop foreign keys from Subscriptions table
ALTER TABLE Subscriptions
DROP FOREIGN KEY subscriptions_ibfk_1, -- subscriber reference
DROP FOREIGN KEY subscriptions_ibfk_2; -- channel reference

-- Step 2: Modify primary key columns to BIGINT

-- Modify Users table
ALTER TABLE Users
MODIFY id BIGINT AUTO_INCREMENT;

-- Modify Videos table
ALTER TABLE Videos
MODIFY id BIGINT AUTO_INCREMENT,
MODIFY owner BIGINT;

-- Modify Comments table
ALTER TABLE Comments
MODIFY id BIGINT AUTO_INCREMENT,
MODIFY video BIGINT,
MODIFY owner BIGINT;

-- Modify Tweets table
ALTER TABLE Tweets
MODIFY id BIGINT AUTO_INCREMENT,
MODIFY owner BIGINT;

-- Modify Likes table
ALTER TABLE Likes
MODIFY id BIGINT AUTO_INCREMENT,
MODIFY video BIGINT,
MODIFY comment BIGINT,
MODIFY tweet BIGINT,
MODIFY likedBy BIGINT;

-- Modify Playlists table
ALTER TABLE Playlists
MODIFY id BIGINT AUTO_INCREMENT,
MODIFY owner BIGINT;

-- Modify PlaylistVideos table
ALTER TABLE PlaylistVideos
MODIFY playlistId BIGINT,
MODIFY videoId BIGINT;

-- Modify Subscriptions table
ALTER TABLE Subscriptions
MODIFY id BIGINT AUTO_INCREMENT,
MODIFY subscriber BIGINT,
MODIFY channel BIGINT;

-- Step 3: Recreate all foreign key constraints

-- Recreate foreign keys for Videos table
ALTER TABLE Videos
ADD CONSTRAINT videos_ibfk_1 FOREIGN KEY (owner) REFERENCES Users(id) ON DELETE SET NULL;

-- Recreate foreign keys for Comments table
ALTER TABLE Comments
ADD CONSTRAINT comments_ibfk_1 FOREIGN KEY (video) REFERENCES Videos(id) ON DELETE CASCADE,
ADD CONSTRAINT comments_ibfk_2 FOREIGN KEY (owner) REFERENCES Users(id) ON DELETE CASCADE;

-- Recreate foreign keys for Tweets table
ALTER TABLE Tweets
ADD CONSTRAINT tweets_ibfk_1 FOREIGN KEY (owner) REFERENCES Users(id) ON DELETE SET NULL;

-- Recreate foreign keys for Likes table
ALTER TABLE Likes
ADD CONSTRAINT likes_ibfk_1 FOREIGN KEY (video) REFERENCES Videos(id) ON DELETE SET NULL,
ADD CONSTRAINT likes_ibfk_2 FOREIGN KEY (comment) REFERENCES Comments(id) ON DELETE SET NULL,
ADD CONSTRAINT likes_ibfk_3 FOREIGN KEY (tweet) REFERENCES Tweets(id) ON DELETE SET NULL,
ADD CONSTRAINT likes_ibfk_4 FOREIGN KEY (likedBy) REFERENCES Users(id) ON DELETE SET NULL;

-- Recreate foreign keys for Playlists table
ALTER TABLE Playlists
ADD CONSTRAINT playlists_ibfk_1 FOREIGN KEY (owner) REFERENCES Users(id) ON DELETE CASCADE;

-- Recreate foreign keys for PlaylistVideos table
ALTER TABLE PlaylistVideos
ADD CONSTRAINT playlistvideos_ibfk_1 FOREIGN KEY (playlistId) REFERENCES Playlists(id) ON DELETE CASCADE,
ADD CONSTRAINT playlistvideos_ibfk_2 FOREIGN KEY (videoId) REFERENCES Videos(id) ON DELETE CASCADE;

-- Recreate foreign keys for Subscriptions table
ALTER TABLE Subscriptions
ADD CONSTRAINT subscriptions_ibfk_1 FOREIGN KEY (subscriber) REFERENCES Users(id) ON DELETE CASCADE,
ADD CONSTRAINT subscriptions_ibfk_2 FOREIGN KEY (channel) REFERENCES Users(id) ON DELETE CASCADE;

-- Optional: Fix the date column types if needed
-- If you want to match java.sql.Date in your models instead of TIMESTAMP
-- Note: This is optional and might not be necessary if your application handles the conversion

-- For Users table
ALTER TABLE Users
MODIFY createdAt DATE,
MODIFY updatedAt DATE;

-- For Videos table
ALTER TABLE Videos
MODIFY createdAt DATE,
MODIFY updatedAt DATE;

-- For Comments table
ALTER TABLE Comments
MODIFY createdAt DATE,
MODIFY updatedAt DATE;

-- For Tweets table
ALTER TABLE Tweets
MODIFY createdAt DATE,
MODIFY updatedAt DATE;

-- For Likes table
ALTER TABLE Likes
MODIFY createdAt DATE,
MODIFY updatedAt DATE;

-- For Playlists table
ALTER TABLE Playlists
MODIFY createdAt DATE,
MODIFY updatedAt DATE;

-- For Subscriptions table
ALTER TABLE Subscriptions
MODIFY createdAt DATE,
MODIFY updatedAt DATE;

DESC Users;
SELECT * FROM users;
SELECT * FROM videos;
SELECT * FROM comments;
SELECT * FROM subscriptions;
DELETE FROM users WHERE id=2;

DROP TABLE Users;
DROP TABLE Videos;
DROP TABLE Comments;
DROP TABLE Tweets; 
DROP TABLE Likes;
DROP TABLE Subscriptions;
DROP TABLE Playlists;
DROP TABLE PlaylistVideos;

ALTER TABLE Likes DROP FOREIGN KEY FKkwis7oqocv58s3vfpj45r4yh9;
ALTER TABLE PlaylistVideos DROP FOREIGN KEY FK4c3vpuvnqhocddw4ibs8bn6h6;
ALTER TABLE PlaylistVideos DROP FOREIGN KEY FKrfldcvlj33fipxg8tb3ibgq94;
