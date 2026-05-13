FROM amazoncorretto:21
# копируем артефакт в образ и переименовываем его в app.jar
COPY target/*.jar app.jar
# указываем, какую команду выполнить при запуске контейнера
ENTRYPOINT ["java","-jar","/app.jar"]