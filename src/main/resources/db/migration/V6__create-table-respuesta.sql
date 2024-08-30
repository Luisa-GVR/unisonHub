CREATE TABLE Respuesta (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mensaje TEXT NOT NULL,
    topico INT,
    fechaCreacion DATETIME NOT NULL,
    autor INT,
    solucion BOOLEAN,
    FOREIGN KEY (topico) REFERENCES Topico(id),
    FOREIGN KEY (autor) REFERENCES Usuario(id)
);
