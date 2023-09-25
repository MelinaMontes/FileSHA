# BoxCustodia
![image](https://github.com/MelinaMontes/BoxCustodia/assets/78213064/524e4f42-4668-4408-af77-573acaaedecb)

Api rest en Java con Springboot + PostgreSQL para la carga de archivos mediante FormData.

Tanto el api como la base de datos se encuentran en un contenedor, lo cual deberia facilitar la ejecucion en cualquier entorno.

Con ' docker compose up -d ' iniciamos los contenedores y luego deberiamos levantar el api desde el IDE.

Servicios: 

POST http://localhost:8080/api/documents/hash
  Body: form-data (files)
  Params: hashType (SHA-256 | SHA-512)

GET http://localhost:8080/api/documents 

GET http://localhost:8080/api/document?hashType=&hash=
  Params: hashType (SHA-256 | SHA-512) ; hash(valor)

  [![Run in Postman](https://run.pstmn.io/button.svg)](https://god.gw.postman.com/run-collection/16169901-e5cc87a1-b3b2-46f4-a28f-f6537f01208d?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D16169901-e5cc87a1-b3b2-46f4-a28f-f6537f01208d%26entityType%3Dcollection%26workspaceId%3D0cf215fb-6675-4116-9a9c-82b011556204)

