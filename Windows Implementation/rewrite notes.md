### Java sockets - rewrite
1. Write: 922µs
2. Read: 514µs

The server socket is now non-blocking
### Mailslots - rewrite
##### init perf tests 425 bytes
1. slot initialisation: +- 320µs
2. slot write: +- 70µs. old: 35µs
3. slot read: +- 40µs

Slower writes maybe because I have the native call wrapped in a "normal" Java method. Also additional validation. C++ code also freeing resources properly now. Anecdotal. 