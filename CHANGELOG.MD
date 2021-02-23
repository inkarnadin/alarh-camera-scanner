# Changelog

All notable changes to this project will be documented in this file.

## UNREALISED

### Changed
- Replace Future tasks to CompletableFuture tasks for the brute scanner.
- Returns NOT_AVAILABLE if host is not available.
- Optimize create and exec brute subtasks.

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
