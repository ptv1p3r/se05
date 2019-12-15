import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'ISMAT SE Android Client',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  var _serviceEnabled;

  void startServiceInPlatform() async {
    if(Platform.isAndroid){
      var methodChannel = MethodChannel("pt.ismat.se05.messages");
      String data = await methodChannel.invokeMethod("startService");

      if (data.isNotEmpty){
        setState(() {
          _serviceEnabled = true;
        });
      }

      debugPrint(data);
    }
  }

  void stopServiceInPlatform() async {
    if(Platform.isAndroid){
      var methodChannel = MethodChannel("pt.ismat.se05.messages");
      String data = await methodChannel.invokeMethod("stopService");

      if (data.isNotEmpty){
        setState(() {
          _serviceEnabled = false;
        });
      }

      debugPrint(data);
    }
  }

  @override
  void initState() {
    _serviceEnabled = false;
  }

  @override
  Widget build(BuildContext context) {

    return Container(
      color: Colors.white,
      child: new Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          _buildServiceStartButton(),
          _buildServiceStopButton(),
        ],
      ),
    );
  }

  Widget _buildServiceStartButton() {
    return new RaisedButton(
      child: new Text(
          _serviceEnabled ? "GPS tracker started!" : "Start GPS tracker"
      ),
      onPressed: _serviceEnabled ? null : startServiceInPlatform,
    );
  }

  Widget _buildServiceStopButton() {
    return new RaisedButton(
      child: new Text(
          _serviceEnabled ? "GPS tracker stopped!" : "Stop GPS tracker"
      ),
      onPressed: _serviceEnabled ? stopServiceInPlatform : null,
    );
  }
}
