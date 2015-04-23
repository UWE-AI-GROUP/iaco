<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
    <head>
        <title>Interactive Ant Colony Optimiser</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script language="JavaScript" type="text/javascript" src="javascript/jquery-1.3.2.js"></script>
        <script src="javascript/Form.js" type="text/javascript"></script>
    </head>
    <body>
        <div>
            <form id="form" name="VariableInput" enctype="multipart/form-data" method="post">
                <div> <p>Designer</p>
                    <select name="Designer" id="Designer">
                        <option>0-Martin</option>
                        <option>1-Stewart</option>
                        <option>2-Jane</option>
                        <option>3-Larry</option>
                        <option>4-Jim</option>
                        <option>5-Julia</option>
                        <option>6-Barry</option>
                        <option>7-Andy</option>
                        <option>8-Will</option>
                        <option>9-Delia</option>
                        <option>10-Chris</option>
                        <option>11-Neil</option>
                    </select>
                    <br>
                </div>
                <div> <p>Design Problem</p>
                    <select name="DesignProblem" id="DesignProblem">
                        <option>Test(randomised)</option>
                        <option>Cinema Booking System</option>
                        <option>Graduate Development Program</option>
                        <option>Select Cruises</option>
                    </select>
                    <br>
                </div>
                <div> <p>Episode Number</p>
                    <select name="EpisodeNumber" id="EpisodeNumber">
                        <option>1</option>
                        <option>2</option>
                        <option>3</option>
                        <option>4</option>
                        <option>5</option>
                        <option>6</option>
                        <option>7</option>
                        <option>8</option>
                        <option>9</option>
                        <option>10</option>
                    </select>
                    <br>
                </div>
                <div> <p>Hints: Freezing & Archiving</p> 
                    <input class="Hints" type="radio" name="Hints:Freezing&Archiving" value="OFF" checked="checked" />OFF
                    <input class="Hints" type="radio" name="Hints:Freezing&Archiving" value="ON" />ON
                    <br>
                </div>
                <div> <p>Colour Metaphor</p>
                    <input class="ColourMetaphor" type="radio" name="ColourMetaphor" value="TrafficLights" checked="checked" />Traffic Lights
                    <input class="ColourMetaphor" type="radio" name="ColourMetaphor" value="WaterTap" />Water Tap    
                    <br>
                </div>
                <div> <p>Name Data File</p>
                    <input id="Filename" type="text" name="Filename" value="Default" size="30" />.dat
                    <br>
                </div>
                <input id="SubmitButton" type="submit" value="Go" name="submitButton" />
                <input id="ConfigButton" type="button" value="Config" name="ConfigButton" disabled="disabled" />
                <input id="BatchButton" type="submit" value="Batch" name="BatchButton" />
            </form>
            <select id="someselect"></select>
        </div>
    </body>
</html>
