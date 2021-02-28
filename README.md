# Camera Scanner

Simple camera vulnerability scanner.
* Finds video streams on open port 554 over specified IP ranges;
* Checks and finds passwords using CVE-2013-4975;
* Realizing brute-force attack through RTSP protocol.

## Usage
### Scanning
* Command `java -jar port-scanner.jar -c source:/home/user/range.txt`. 
* Add `-p` flag for set scanning port (554 by default).
* Add `-t` flag for set parallel threads (25 by default).

### BruteForce RTSP
* Command `java -jar port-scanner.jar -b source:/home/user/list.txt passwords:/home/user/pass.txt`.
* Add `-uc` flag - checking even socket connection unstable.
* Add `-t` flag for set parallel threads (25 by default).

### BruteForce Auth
* Command `java -jar port-scanner.jar -ba source:/home/user/list.txt passwords:/home/user/pass.txt`.

### Results
* All results will be saved in the path `/results/...`.
* All common logs will be saved in the path `/logs/out.log`.

## Save stream
* An example command for receiving a video stream:

 `ffmpeg -i rtsp://${login}:${password}@${host}/Streaming/Channels/101 -acodec copy -vcodec copy /home/user/video.mp4`.