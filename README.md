# AndroidSidePanel

![Generic badge](https://img.shields.io/badge/version-v1.0.1-blue.svg)
![Generic badge](https://img.shields.io/badge/API-+21-orange.svg)

<img width="551" alt="스크린샷 2022-01-23 오전 7 13 15" src="https://user-images.githubusercontent.com/57319751/150657120-4ed8ffac-7832-42d9-bb5a-f57b2980d6c2.png">

It is a Side Panel library like the Edge Panel of Galaxy.
You can draw a layout using xml and access it like a general View.

# ⚙️ How to set

### Gradle

1. Add the JitPack repository to your build file.
    
    ```gradle
    allprojects {
    	repositories {
    		...
    		maven { url 'https://jitpack.io' }
    	}
    }
    ```
    
2. Add the dependency
    
    ```gradle
    dependencies {
    	 implementation 'com.github.dddh1221:AndroidSidePanel:vX.Y.Z'
    }
    ```
    

### Maven

1. Add the JitPack repository to your build file
    
    ```maven
    <repositories>
    	<repository>
    	    <id>jitpack.io</id>
    	    <url>https://jitpack.io</url>
    	</repository>
    </repositories>
    ```
    
2. Add the dependency
    
    ```maven
    <dependency>
    	   <groupId>com.github.dddh1221</groupId>
    	   <artifactId>AndroidSidePanel</artifactId>
    	   <version>vX.Y.Z</version>
    </dependency>
    ```
    
# 🤔 How to use?

1. Design your xml like this: Draw a SidePanelLayout on top of all views. And put SidePanelView inside Layout. SidePanelLayout will act as the background of the side panel, and SidePanelView will act as the actual side panel. 
Other widgets can also be added within the Layout. Widgets other than SidePanelView are treated as a background and the same Alpha value is applied.
    
    > One thing to note here is that the Side Panels should always be glued to the right side. If you put a SidePanelView as a SidePanelLayout child view, it will automatically position itself. A SidePanelView should not have a Margin on the right side.
    > 
    
    ```xml
    <com.dahun.sidepanel.widget.SidePanelLayout
    	android:id="@+id/panel_layout"
    	android:layout_width="match_parent"
    	android:layout_height="match_parent"
    	app:defaultClosed="true"
    	app:dimBackgroundColor="@color/gray">
    
    	<!-- Here you can add a widget to use as a background. -->
    
    	<com.dahun.sidepanel.widget.SidePanelView
    		android:id="@+id/panel_view"
    		android:layout_width="match_parent"
    		android:layout_height="match_parent">
    
    		<!-- Design your side panels here. -->
    
    	</com.dahun.sidepanel.widget.SidePanelView>
    
    </com.dahun.sidepanel.widget.SidePanelLayout>
    ```
    
2. Draw a View to use as a slider to open and close the side panel.
    
    ```xml
    <ImageView
    	android:id="@+id/panel_slider"
    	android:layout_width="wrap_content"
    	android:layout_height="wrap_content"
    	app:src="@drawable/slider"/>
    ```
    
3. Connect the SidePanelLayout with the view used as a slider.
    
    ```kotlin
    val panelLayout = findViewById<SidePanelLayout>(R.id.panel_layout)
    val panelSlider = findViewById<ImageView>(R.id.panel_slider)
    
    panelLayout.bindSlider(panelSlider)
    ```

4. Result

  ![Jan-23-2022 07-11-55](https://user-images.githubusercontent.com/57319751/150657149-674340a7-a3d2-43b8-aa9a-92f54d67c24a.gif)

# 💬 Detail

### Kotiln

- `setPanelSlideSensitive(Float)` : Sets the slide sensitivity of the panel. Sensitivity here refers to the sensitivity to open or closed perception. It can be set from 0.0 to 1.0, and the higher the number, the more sensitive it is to small slides.
- `setDimBackgroundMaxAlpha(Float)` : Set the maximum Alpha value of Dim Background. It means the maximum value of the alpha value that changes according to the slide position of the panel.
- `setDimBackgroundColor(Int)` : Change the color of the Dim Background.
- `getPanelState()` : Gets the current state of the panel.
    - `SidePanelGestureController.STATE_CLOSED` : Panel closed
    - `SidePanelGestureController.STATE_MOVED` : Panel is moving (by Controller)
    - `SidePanelGestureController.STATE_DRAGGING` : Panel is moving (by user)
    - `SidePanelGestureController.STATE_OPENED` : Panel opened
- `showPanel()` : Open the panel.
- `dismissPanel()` : Close the panel.
- `bindSlider(View)` : Connect with the slider.

### XML

The above settings are also available in xml.

```xml
<com.dahun.sidepanel.widget.SidePanelLayout
	android:id="@+id/panel_layout"
	android:layout_width="match_parent"
	android:layout_height="match_parent"
	app:defaultClosed="true
	app:sliderSensitive="0.7"
	app:maxDimAlpha="0.8"
	app:dimBackgroundColor="@color/gray">

	<!-- 
		app:defaultClosed : Sets the initial state of the panel to close.
	 -->

</com.dahun.sidepanel.widget.SidePanelLayout>
```

# ©️ License

```xml
Copyright 2022 Dahun Kim.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
