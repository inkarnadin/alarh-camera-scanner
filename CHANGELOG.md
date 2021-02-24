# Changelog

All notable changes to this project will be documented in this file.

## UNREALISED

### Added
- Add sub thread name.
- Add Context for every brute address. It contains info about properly path of checking.
- Add flag `-uc`. If toggle it checked even connection unstable which returns errors.
- Add flag `-t` for changing count of active threads.

### Changed
- Replace Future tasks to CompletableFuture tasks for the brute scanner.
- Replace Future tasks to CompletableFuture tasks for the camera scanner.
- Returns NOT_AVAILABLE if host is not available.
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
