# Changelog

All notable changes to this project will be documented in this file.

## [UNRELEASED]

### Changed
- Changed result output camera file: camera.txt to hosts.txt.

## [0.4.0] - 17-11-2021

### Added
- Added Travis CI config file.
- Added LICENSE.txt.
- Added additional check password from CVE for the same range.
- Added preliminary total addresses count checking. 
- Added screen saving via ONVIF for cameras with CVE-based found passwords.

### Changed
- Substitute report format to HTML instead plain text.
- Changed bruted data format.

### Fixed
- Fixed wrong deleting default credentials when using CVE vulnerability.
- Fixed wrong report template loading.
- Fixed OOM if too many addresses trying check per time.
- Fixed issue when creation report when expected time was too big.

## [0.3.0] - 22-05-2021

### Added
- Added flag `-t` waiting timeout host response (sec, default 500 ms).
- Added support for large ip ranges.
- Added ffmpeg log.
- Added statistic info about ffmpeg frame saving.
- Added scanning statistic data to report.
- Added efficiency statistic.
- Added time statistic.
- Added analyze ffmpeg log and re-run some problems target.
- Added restore scanning if it was interrupted.
- Added `-nb` flag for disable brute.
- Added `-nc` flag for disable scanning port.

### Changed
- The flag `-th` instead `-t` now.
- The flag `-sf` instead `-screen`.
- The flag `-w` instead `-t`.
- The flag `-bw` instead `-w`.
- Updated special RTSP path as *11* instead *Streaming/Channels/101*.
- If CVE credentials not found return certain message instead list of all found words.
- Changed the mechanism of counting all IP addresses that will be scanned.
- Changed unit for timeout flags - `-t` and `-w` for milliseconds instead second.
- Optimized ip range working.
- CVE log as part of brute log now.
- Global remake save frame via ffmpeg mechanism.
- The processing mechanism has been changed - now each range check is being for the presence of addresses, 
and then immediately for password guessing (before, all ranges or all addresses check is being).

### Fixed
- Fixed some log naming typos.
- Fixed hanging external processes.
- Fixed critical thread pool bug.

### Removed
- Removed `-b`, `-ba` and `-c` flags.
- Removed auth basic brute.

## [0.2.0] - 17-03-2021

### Changed
- Changed CVE credentials output format.
- Increased socket connection timeout.

### Added
- Added BasicAuth scanner (flag `-ba`).
- Added flag `-bw` brute waiting timeout (sec, default 2).
- Added missing `javadoc`.
- Added separate parser for socket responses.
- Added IP not available detail message.
- Added cyrillic support for CVE parse a password.
- (*Experimental*) Added saving screenshot function.

### Fixed
- No longer tries to send a message if the socket connection failed.
- Minor corrections of `javadoc`.

## [0.1.0] - 26-02-2021

### Added
- Added sub thread name.
- Added Context for every brute address. It contains info about properly path of checking.
- Added flag `-uc`. If toggle it checked even connection unstable which returns errors.
- Added flag `-t` for changing count of active threads.
- Added flag `-a` flag to set the socket reconnection limit on failure.
- Added brute force `javadoc`.

### Changed
- The brute force attack mechanism has been updated. Now you do not need to specify many threads, the enumeration occurs through `n` 
the number of open sockets that try to reconnect in case of failure a specified number of times. This reduces the load 
on the target host and improves attack performance. At the same time, with a few threads and a large password dictionary, 
the cost of an error increases if one of the subtasks fails to connect.
- In connection with the change in the brute-force mechanism, it became possible to sort the password through large 
dictionaries without overloading the server. It is desirable that the connection to the server is stable. 
- Replace Future tasks to CompletableFuture tasks for the brute scanner.
- Replace Future tasks to CompletableFuture tasks for the camera scanner.
- Returns NOT_AVAILABLE when socket connection problems.
- Optimized creation and execution of brute subtasks.
- Change some log messages.
- Update README description.

### Removed
- Removed flag `-r`. List of source no longer cleaned.

### Fixed
- Fix brute force blocking when server is not responding and read stream is busy.

## [0.0.1] - 21-02-2021

### Added 
- Added changelog file.
- Added `-p` flag for input scanning port.

### Changed
- Modified check algorithm brute-force checking for an empty credential.
- Repair preferences usage.

### Fixed
- A partial fix for brute force blocking when server is not responding and read stream is empty.

[unreleased]: https://github.com/inkarnadin/alarh-camera-scanner/compare/v0.4.0...HEAD
[0.4.0]: https://github.com/inkarnadin/alarh-camera-scanner/releases/tag/v0.4.0
[0.3.0]: https://github.com/inkarnadin/alarh-camera-scanner/releases/tag/v0.3.0
[0.2.0]: https://github.com/inkarnadin/alarh-camera-scanner/releases/tag/v0.2.0
[0.1.0]: https://github.com/inkarnadin/alarh-camera-scanner/releases/tag/v0.1.0