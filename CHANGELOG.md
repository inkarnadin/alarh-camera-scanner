# Changelog

All notable changes to this project will be documented in this file.

## UNREALISED

### Added
- Add sub thread name.
- Add Context for every brute address. It contains info about properly path of checking.
- Add flag `-uc`. If toggle it checked even connection unstable which returns errors.
- Add flag `-t` for changing count of active threads.
- Add flag `-a` for set limit reconnection socket in failure case.
- Add brute force `javadoc`.

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
- Add `-p` flag for input scanning port.

### Changed
- Modified check algorithm brute-force checking for an empty credential.
- Repair preferences usage.

### Fixed
- A partial fix for brute force blocking when server is not responding and read stream is empty.