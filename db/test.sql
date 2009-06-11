DROP TABLE Test IF EXISTS;

CREATE TABLE Test(id INT IDENTITY, key INT, data VARCHAR(50));

INSERT INTO Test (id, key, data) VALUES (1, 1, 'Test1');
INSERT INTO Test (id, key, data) VALUES (2, 2, 'Test2');
INSERT INTO Test (id, key, data) VALUES (3, 3, 'Test3');
INSERT INTO Test (id, key, data) VALUES (4, 4, 'Test4');
INSERT INTO Test (id, key, data) VALUES (5, 5, 'Test5');
INSERT INTO Test (id, key, data) VALUES (6, 6, 'Test6');
INSERT INTO Test (id, key, data) VALUES (7, 7, 'Test7');

SELECT * FROM Test;

DROP TABLE TestXML IF EXISTS;

CREATE TABLE TestXML(id INT IDENTITY, key INT, data VARCHAR(50), xmlData VARCHAR(100));

SELECT * FROM TestXML;