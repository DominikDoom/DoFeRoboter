# DoFeRoboter
The repository for our leJOS EV3 robot project.
This project was created as a graded project work for the [Technische Hochschule Mittelhessen](https://www.thm.de/).

It is designed for usage with the [Plott3r](http://www.jander.me.uk/LEGO/plott3r.html) EV3 model and the leJOS EV3 runtime environment.

# Setup
This project is separated into two parts, this repo being the backend.
Frontend can be found [here](https://github.com/rabitem/DoFe-Roboter-Frontend).

Run `mvn install` to compile & upload the jar to the brick.

If no Lego EV3 brick & physical Plott3r construct is available, a simple visualizer can be used instead.
For this, change the following line in Main.java from
```java
public static void main(String[] args) {
    robot = new Robot();
    ...
}
```
to
```java
public static void main(String[] args) {
    robot = new Visualizer();
    ...
}
```
