CREATE TABLE Topico (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    mensaje TEXT NOT NULL,
    fechaCreacion DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    autor INT,
    curso INT,
    FOREIGN KEY (autor) REFERENCES Usuario(id),
    FOREIGN KEY (curso) REFERENCES Curso(id)
);