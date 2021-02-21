# Camera Scanner

Simple camera vulnerability scanner.
* Finds video streams on open port 554 over specified IP ranges;
* Checks and finds passwords using CVE-2013-4975;
* Realizing brute-force attack through RTSP protocol.

## Usage
### Scanning cameras
* Command `java -jar port-scanner.jar -c source:/home/user/range.txt`. 
* Add `-p` flag for set scanning port (554 by default).

### BruteForce
* Command `java -jar port-scanner.jar -b source:/home/user/list.txt passwords:/home/user/pass.txt`.
* Add `-r` flag for removed unreliable and slowly hosts from list. 

## Save stream
* An example command for receiving a video stream:

 `ffmpeg -i rtsp://${login}:${password}@${host}/Streaming/Channels/101 -acodec copy -vcodec copy /home/user/video.mp4`.