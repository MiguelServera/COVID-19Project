# COVID-19Project

Para testear se necesita:
- API en local de mi compañero Álvaro Maleno.
- Cambiar la url de conexión a la que proporciona Conveyor en local y poner /covidTest.
- Dar permisos totales a la aplicación.
- Tener wifi la primera vez que hagamos login.
- La barra lateral aún no está implementada
- Para asegurar un funcionamiento estable, tener conexión a WiFi (No datos móviles al no ser que solo queramos la informacion de las comunidades autónomas actualizadas).

Manual de instalación:
Tener Android Studio y Microsoft Visual Studio instalados.

Clonar tanto la API de mi compañero Álvaro Maleno como mi código.

Seguir la siguiente documentación para poder iniciar la API en local, gracias a Conveyor, una extensión gratuíta.
https://docs.google.com/document/d/1iDS26o_RT1I2XIMJ2nG-cI0oYHTTz9wuNUjqTsmCQJw/edit

Abrir el proyecto clonado de la API con Visual Studio e iniciar la solución de "Covid".
Abrir el proyecto clonado de Android y cambiar la url de conexión en la clase "RetrieveStatsFromAPI" a la url que proporciona Conveyor antes de iniciar la app.

# Inciar la app:

Al iniciar la aplicación nos pedirá permisos para poder bajarnos un archivo CSV con los datos de las comunidades autónomas, le cedemos el permiso.

Para poder hacer login es necesario anteriormente tener un usuario registrado, el cual podemos registrar apretando el botón de "Register". Una vez nos salga un Toast diciendo que está insertado, ya podemos hacer login con el usuario insertado y probar la aplicación.

Cómo he mencionado antes, es recomendado tener WIFI o como mínimo, datos móviles para poder bajarno almenos el CSV de las comunidades autónomas. Cada día estos datos se irán actualizando.

(Puede haber errores!) 

(Postearé imágenes en un futuro)
