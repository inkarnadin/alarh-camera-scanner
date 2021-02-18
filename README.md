# Camera Scanner

Simple camera vulnerability scanner.
* Finds cameras on open port 8000 over specified IP ranges;
* Checks and finds passwords using CVE-2013-4975;
* Checks passwords by brute force.

## Usage
### Scanning cameras
* Command `java -jar port-scanner.jar -c source:/home/user/range.txt`.  

### BruteForce
* Command `java -jar port-scanner.jar -b source:/home/user/list.txt passwords:/home/user/pass.txt`
* Add `-r` flag for removed unreliable and slowly hosts from list 

## Save stream
* Command `ffmpeg -i rtsp://${login}:${password}@${host}/Streaming/Channels/101 -acodec copy -vcodec copy /home/user/video.mp4
`