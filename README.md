# BoxCustodia
![image](https://github.com/MelinaMontes/BoxCustodia/assets/78213064/354d0612-64e7-4e08-8236-c18582d472ee)

Api rest para la carga de archivos mediante FormData.

Tanto el api como la base de datos se encuentran en un contenedor, lo cual deberia facilitar la ejecucion en cualquier entorno.

Con ' docker compose up -d ' iniciamos los contenedores y luego deberiamos levantar el api desde el IDE.

Servicios: 

-POST http://localhost:8080/api/documents/hash
  Body: form-data (files)
  Params: hashType (SHA-256 | SHA-512)
-
  
-GET http://localhost:8080/api/documents 
-

-GET http://localhost:8080/api/document?hashType=&hash=
  Params: hashType (SHA-256 | SHA-512) ; hash(valor)
-
