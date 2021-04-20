# Changelog

All notable changes to this project will be documented in this file.

## UNRELEASED

### Changed
- Updated special RTSP path as 11 instead Streaming/Channels/101.
- If CVE credentials not found return certain message instead list of all found words.
- Changed the mechanism of counting all IP addresses that will be scanned.

### Fixed
- Fixed some log naming typos.

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
