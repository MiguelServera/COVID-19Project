# COVID-19Project

Para testear se necesita:
- API en local de mi compañero Álvaro Maleno.
- Cambiar la url de conexión a la que proporciona Conveyor en local y poner /api/covidTest.
- Dar permisos totales a la aplicación.
- Tener wifi la primera vez que hagamos login.
- Para asegurar un funcionamiento estable, tener conexión a WiFi (No datos móviles al no ser que solo queramos la informacion de las comunidades autónomas actualizadas).

Manual de instalación:
Tener Android Studio y Microsoft Visual Studio instalados.

Clonar tanto la API de mi compañero Álvaro Maleno como mi código.

Seguir la siguiente documentación para poder iniciar la API en local, gracias a Conveyor, una extensión gratuíta.
https://docs.google.com/document/d/1iDS26o_RT1I2XIMJ2nG-cI0oYHTTz9wuNUjqTsmCQJw/edit

Abrir el proyecto clonado de la API con Visual Studio e iniciar la solución de "Covid". Atención! Seguir los pasos para instalar la API en el GitHub de Álvaro: https://github.com/alvaroMaleno/CovidAPI.git
Abrir el proyecto clonado de Android y cambiar la url de conexión en la clase "RetrieveStatsFromAPI" y en la clase "ShowCountryInfo" a la url que proporciona Conveyor, añadiendo "/api/covidTest" antes de iniciar la app.

# Iniciar la app:

Al iniciar la aplicación nos pedirá permisos para poder bajarnos un archivo CSV con los datos de las comunidades autónomas, le cedemos el permiso.

Para poder hacer login es necesario anteriormente tener un usuario registrado, el cual podemos registrar apretando el botón de "Register". Una vez nos salga un Toast diciendo que está insertado, ya podemos hacer login con el usuario insertado y probar la aplicación.

Cómo he mencionado antes, es recomendado tener WIFI o como mínimo, datos móviles para poder bajarnos almenos el CSV de las comunidades autónomas. Cada día estos datos se irán actualizando.

# Errores que pueden surgir durante el inicio de la aplicación:

Si se ejecuta la API en local y tenemos WIFI en nuestro dispositivo no habrá problema alguno, ahora bien, si no ejecutamos la API, la aplicación si detecta WIFI intentará bajar estos datos pero como no habrá nada que recoger, solo los datos CSV se verán actualizados/insertados, de forma que, al ir a ver la información de cualquier otra tabla/gráfica, estas no se mostrarán.
De igual forma al entrar en el WIFI local de la API, con la IP que toca, la aplicación volverá a intentarlo.

En el botón de "share" en la barra lateral, seleccionar, si se desea, el segundo icono de Whatsapp, no el primero, ya que por algún motivo este no funciona.

La aplicación está hecha de forma qué, se actualice automáticamente cada día, controla si ha pasado un día y si hay internet para descargar los datos de países y comunidades autónomas, así que no tendría que haber problema.

Por algún motivo, la API devuelve valores negativos en algunos casos, según el servidor de la API, Álvaro Maleno, es porque los países corrigen datos de otras fechas. Aunque la mayoría de los casos están bien y con los numeros que tocan.

Todo lo demás, debería de funcionar.

Gracias por probar la app!


