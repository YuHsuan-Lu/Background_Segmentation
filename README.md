# Background_Segmentation <badge>![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)<badge></br>
 #Java #background_segmentation #image_segmentation #background_subtraction </br>
## Video Demo
![ezgif-1-b0d347a9d8](https://user-images.githubusercontent.com/98661228/195191338-be2aa680-ad83-41e6-9c60-96456d53905f.gif)


## Command
### Fist compile the file</br></br>

```
 javac imageSegementation.java
```

### Then run</br>

```
Java imageSegementation basename_of_foreground base_name_of_background mode1
```

 or
 
``` 
Java imageSegementation basename_of_background_subtraction base_name_of_background mode0
```
#### **** mode 0 is used to extract objects from green screen video </br>
#### **** while mode 1 is used to extract objects from a real world video </br>
#### **** Note that basename should include the path to the file</br>
#### **** (futher explantion see below section "Basename explanation")</br>

To make it easier to understand, we can plot it as a flow chart
```mermaid
graph LR;
    imageSegementation-->foreground(mode 1);
    imageSegementation-->background_subtraction(mode 0);
    foreground(mode 1)-->background;
    background_subtraction(mode 0)-->background;
    background-->output_video;
```
## Use sample input to test
You can find these files in the corresponding repository</br>
You can also try out any video you like</br></br>
### Basename explanation
For the command line you have to type in the basename of the frames including its path</br>
for example, if you have 480 frames of foreground file</br>
ranging from "aaa/bbb/ccc_ccc.0000.rgb" to "aaa/bbb/ccc_ccc.0479.rgb" </br>
then you should type in "aaa/bbb/ccc_ddd"</br>
same goes with background file</br></br></br>
