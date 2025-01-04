use foodapp;
-- Create table for User
CREATE TABLE User (
    UserId INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(100) NOT NULL,
    Phone VARCHAR(15)
);

-- Create table for Address
CREATE TABLE Address (
    AddId INT PRIMARY KEY AUTO_INCREMENT,
    UserId INT NOT NULL,
    DefaultAddress BOOLEAN,
    City VARCHAR(100),
    AddressDetail TEXT,
    Coordinate VARCHAR(100),
    FOREIGN KEY (UserId) REFERENCES User(UserId)
);

-- Create table for Order
CREATE TABLE `Order` (
    OrderId INT PRIMARY KEY AUTO_INCREMENT,
    AddressId INT NOT NULL,
    Status VARCHAR(50),
    FOREIGN KEY (AddressId) REFERENCES Address(AddId)
);

-- Create table for Item
CREATE TABLE Item (
    ItemId INT PRIMARY KEY AUTO_INCREMENT,
    RestaurantId INT NOT NULL,
    Name VARCHAR(100),
    Price DECIMAL(10, 2),
    Photo TEXT,
    FoodDetail TEXT,
    FOREIGN KEY (RestaurantId) REFERENCES Restaurant(RestaurantId)
);

-- Create table for ItemCategory
CREATE TABLE ItemCategory (
    ItemCategoryId INT PRIMARY KEY AUTO_INCREMENT,
    ItemId INT NOT NULL,
    CategoryId INT NOT NULL,
    FOREIGN KEY (ItemId) REFERENCES Item(ItemId),
    FOREIGN KEY (CategoryId) REFERENCES Category(CategoryId)
);

-- Create table for Category
CREATE TABLE Category (
    CategoryId INT PRIMARY KEY AUTO_INCREMENT,
    CategoryName VARCHAR(100)
);

-- Create table for Restaurant
CREATE TABLE Restaurant (
    RestaurantId INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(100),
    Photo TEXT,
    MinPurchase DECIMAL(10, 2),
    City VARCHAR(100),
    Address TEXT,
    Coordinate VARCHAR(100)
);

-- Create table for DeliveryFee
CREATE TABLE DeliveryFee (
    FeeId INT PRIMARY KEY AUTO_INCREMENT,
    RestaurantId INT NOT NULL,
    Price DECIMAL(10, 2),
    TenderArea BOOLEAN,
    FOREIGN KEY (RestaurantId) REFERENCES Restaurant(RestaurantId)
);

-- Create table for WorkingTime
CREATE TABLE WorkingTime (
    WorkingTimeId INT PRIMARY KEY AUTO_INCREMENT,
    RestaurantId INT NOT NULL,
    Day VARCHAR(20),
    OpenAt TIME,
    CloseAt TIME,
    FOREIGN KEY (RestaurantId) REFERENCES Restaurant(RestaurantId)
);

-- Create table for OrderDetail
CREATE TABLE OrderDetail (
    OrderDetailId INT PRIMARY KEY AUTO_INCREMENT,
    Price DECIMAL(10, 2),
    Count INT,
    OrderId INT NOT NULL,
    ItemId INT NOT NULL,
    FOREIGN KEY (OrderId) REFERENCES `Order`(OrderId),
    FOREIGN KEY (ItemId) REFERENCES Item(ItemId)
);

-- Create table for Feedback
CREATE TABLE Feedback (
    FeedbackId INT PRIMARY KEY AUTO_INCREMENT,
    OrderDetailId INT NOT NULL,
    Rate DECIMAL(2, 1),
    Description TEXT,
    FOREIGN KEY (OrderDetailId) REFERENCES OrderDetail(OrderDetailId)
);

-- Create table for ShoppingCart
CREATE TABLE ShoppingCart (
    CartId INT PRIMARY KEY AUTO_INCREMENT,
    UserId INT NOT NULL,
    ItemId INT NOT NULL,
    Date DATETIME,
    Count INT,
    FOREIGN KEY (UserId) REFERENCES User(UserId),
    FOREIGN KEY (ItemId) REFERENCES Item(ItemId)
);

-- Insert data into User
INSERT INTO User (Name, Phone) VALUES
('Ali', '12345'),
('Sara', '54321'),
('Reza', '98765'),
('Mina', '67890'),
('Hassan', '11111');

-- Insert data into Address
INSERT INTO Address (UserId, DefaultAddress, City, AddressDetail, Coordinate) VALUES
(1, TRUE, 'Shiraz', 'Street A, Block B', '29.6, 52.5'),
(2, FALSE, 'Tehran', 'Street C, Block D', '35.7, 51.4'),
(3, TRUE, 'Mashhad', 'Street E, Block F', '36.3, 59.6'),
(4, FALSE, 'Isfahan', 'Street G, Block H', '32.6, 51.7'),
(5, TRUE, 'Tabriz', 'Street I, Block J', '38.1, 46.2');

-- Insert data into Order
INSERT INTO `Order` (AddressId, Status) VALUES
(1, 'Done'),
(2, 'Pending'),
(3, 'In Progress'),
(4, 'Delivered'),
(5, 'Cancelled');

-- Insert data into Restaurant
INSERT INTO Restaurant (Name, Photo, MinPurchase, City, Address, Coordinate) VALUES
('Pizza Palace', 'photo1.jpg', 100, 'Shiraz', 'Street A', '29.6, 52.5'),
('Burger House', 'photo2.jpg', 200, 'Tehran', 'Street B', '35.7, 51.4'),
('Sushi World', 'photo3.jpg', 300, 'Mashhad', 'Street C', '36.3, 59.6'),
('Pasta Corner', 'photo4.jpg', 150, 'Isfahan', 'Street D', '32.6, 51.7'),
('Dessert Haven', 'photo5.jpg', 120, 'Tabriz', 'Street E', '38.1, 46.2');

-- Insert data into Item
INSERT INTO Item (RestaurantId, Name, Price, Photo, FoodDetail) VALUES
(1, 'Margherita Pizza', 150.00, 'pizza1.jpg', 'Cheese and Tomato'),
(2, 'Beef Burger', 200.00, 'burger1.jpg', 'Juicy beef patty'),
(3, 'Salmon Sushi', 300.00, 'sushi1.jpg', 'Fresh salmon rolls'),
(4, 'Alfredo Pasta', 180.00, 'pasta1.jpg', 'Creamy sauce'),
(5, 'Chocolate Cake', 120.00, 'dessert1.jpg', 'Rich chocolate flavor');

-- Insert data into ItemCategory
INSERT INTO ItemCategory (ItemId, CategoryId) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);

-- Insert data into Category
INSERT INTO Category (CategoryName) VALUES
('Pizza'),
('Burger'),
('Sushi'),
('Pasta'),
('Dessert');

-- Insert data into DeliveryFee
INSERT INTO DeliveryFee (RestaurantId, Price, TenderArea) VALUES
(1, 50.00, TRUE),
(2, 60.00, FALSE),
(3, 70.00, TRUE),
(4, 40.00, FALSE),
(5, 30.00, TRUE);

-- Insert data into WorkingTime
INSERT INTO WorkingTime (RestaurantId, Day, OpenAt, CloseAt) VALUES
(1, 'Monday', '10:00:00', '22:00:00'),
(2, 'Tuesday', '09:00:00', '21:00:00'),
(3, 'Wednesday', '11:00:00', '23:00:00'),
(4, 'Thursday', '10:00:00', '22:00:00'),
(5, 'Friday', '08:00:00', '20:00:00');

-- Insert data into OrderDetail
INSERT INTO OrderDetail (Price, Count, OrderId, ItemId) VALUES
(150.00, 2, 1, 1),
(200.00, 1, 2, 2),
(300.00, 3, 3, 3),
(180.00, 1, 4, 4),
(120.00, 5, 5, 5);

-- Insert data into Feedback
INSERT INTO Feedback (OrderDetailId, Rate, Description) VALUES
(1, 5.0, 'Excellent taste!'),
(2, 4.5, 'Very good.'),
(3, 4.0, 'Fresh and tasty.'),
(4, 3.5, 'Average taste.'),
(5, 5.0, 'Amazing dessert!');

-- Insert data into ShoppingCart
INSERT INTO ShoppingCart (UserId, ItemId, Date, Count) VALUES
(1, 1, '2025-01-04 10:00:00', 1),
(2, 2, '2025-01-04 11:00:00', 2),
(3, 3, '2025-01-04 12:00:00', 3),
(4, 4, '2025-01-04 13:00:00', 4),
(5, 5, '2025-01-04 14:00:00', 5);

select * from user;