# Guard-Dog
Mobile App help find lost dogs.

Español:
Esta APP Nativa (Android Studio y Kotlin + Firebase) la he realizado como mi proyecto de fin de GFPS como técnico DAM.
Se trata de una app que intenta crear una comunidad donde tanto personas que han perdido a sus peludos como personas que ven peludos por la calle y no les cuadra, tengan un punto de encuentro y puedan ayudarse mutuamente a que peludo y familia se encuentren.

Es un MVP, así que tiene el diseño y las funcionalidades imprescindibles y básicas para cumplir con su función primando las buenas prácticas y que el proyecto sea escalable a futuro desde el minuto 0.

Como podrás ver, en este MVP he intentado no usar las librerias mínimas he imprescindibles (sólo Picasso y el compendio de Firebase).

Desde esta APP podras realizar tareas de consulta (anuncios de perros perdidos y Links de interes) y escritura (creación de anuncios *incluyendo subida de imágenes a la BBDD*).

Para almacenar todo lo necesario para que la APP sea funcional en cualquier dispositivo Android con una versión superior a la 23, me decanté por usar Firebase. 
¿Por qué?
Porque es una BBDD *gratuita*, super intuitiva, en la nube y a la que, sobre todo, tenia ganaas de meterle mano ya que la amplitud de herramientas como Authentication, Analytics, Crashlytics, etc... van mucho más allá del mero almacenamiento de bytes, ya que te permiten tener una visión sobre el estado de tu aplicación descomunal (tanto técnicamente como comercialmente).

Me llevo la espinita (a día 29/04/2023) de no poder haber dejado hechos los Test Unitarios, pero tranquilo, la APP se ha probado de todas las formas posibles en cada iteración teniendome a mí en el papel de QA.

Espero te parezca un buen primer paso quizas hacia algo más grande y complejo.

Actualmente versión Beta:
V1.0 incluye: 
->Consulta de noticias individualizadas con imagen y enlace a Google Maps.
->Consulta de Links con web view asociado.
->Creación de anuncios propios.
->Eliminación de anuncios propios.
->Dos formas de iniciar sesion: Registro de mail y contraseña y con la cuenta de Google.
->Logout e inicio automatico de la sesión sin tener que hacer login de nuevo (a no ser que se haga logout o unistall)
->Iconos personalizados.


Pronto release en la Play Store.


Adjunto repositorio en Github donde podrás encontrar Guard Dog:
https://github.com/Martin4691/Guard-Dog

Y adjunto mi LinkedIn por si quieres contactarme para preguntarme por este proyecto, otro de los que puedes encontrar en mi repositorio personal de GitHub o tienes alguna duda en la que te pueda echar una mano:
https://www.linkedin.com/in/mart%C3%ADns%C3%A1nchez/



Muhcas gracias por llegar hasta aquí, espero que te haya gustado Guar Dog.
Un saludo.
