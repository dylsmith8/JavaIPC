# Author: Dylan Smith
# Date: 25 June 2016
# Modified: 2 August 2016
# R script that generates stats and graphs based on IPC results

# NAMED PIPES
# =============================================================
  # DATA (IN NS)
NAMEDPIPES_MEAN_40_BYTES <- 907855.3
NAMEDPIPES_MEAN_400_BYTES <- 944254.1
NAMEDPIPES_MEAN_4000_BYTES <- 896791.9
NAMEDPIPES_MEAN_40000_BYTES <- 968381.4
  # IN NANO SECONDS
cat("Named pipe mean in NS (40 BYTES): \t\t\t", NAMEDPIPES_MEAN_40_BYTES)
  print("")
cat("Named pipe mean in NS (400 BYTES): \t\t\t", NAMEDPIPES_MEAN_400_BYTES)
  print("")
cat("Named pipe mean in NS (4 000 BYTES): \t\t\t", NAMEDPIPES_MEAN_4000_BYTES)
  print("")
cat("Named pipe mean in NS (40 000 BYTES): \t\t\t", NAMEDPIPES_MEAN_40000_BYTES)
  print("")
  # IN micro-seconds SECONDS
cat("Named pipe mean in micro-seconds (40 BYTES): \t\t", NAMEDPIPES_MEAN_40_BYTES / 100)
  print("")
cat("Named pipe mean in micro-seconds (400 BYTES): \t\t", NAMEDPIPES_MEAN_400_BYTES / 100)
  print("")
cat("Named pipe mean in micro-seconds (4 000 BYTES): \t", NAMEDPIPES_MEAN_4000_BYTES / 100)
  print("")
cat("Named pipe mean in micro-seconds (40 000 BYTES): \t", NAMEDPIPES_MEAN_40000_BYTES / 100)
  print("")

print("==========================================================")

# MAILSLOTS (JNI ONLY)
# ===============================================================
  # DATA (IN NS)
MAILSLOTS_JNI_40_BYTES <- 665939
MAILSLOTS_JNI_400_BYTES <- 870890
MAILSLOTS_JNI_4000_BYTES <- 971590.7
MAILSLOTS_JNI_40000_BYTES <- 983258.1
  #  IN NANO SECONDS
cat("Mailslots (JNI only) mean in NS (40 bytes):", MAILSLOTS_JNI_40_BYTES)
  print("")
cat("Mailslots (JNI only) mean in NS (400 bytes)", MAILSLOTS_JNI_400_BYTES)
  print("")
cat("Mailslots (JNI only) mean in NS (40 00 bytes)", MAILSLOTS_JNI_4000_BYTES)
  print("")
cat("Mailslots (JNI only) mean in NS (40 000 bytes)", MAILSLOTS_JNI_40000_BYTES)
  print("")
  # IN MICRO-SECONDS
cat("Mailslots (JNI only) mean in MICRO-SECONDS (40 bytes)", MAILSLOTS_JNI_40_BYTES / 100)
  print("")
cat("Mailslots (JNI only) mean in MICRO-SECONDS (400 bytes)", MAILSLOTS_JNI_400_BYTES / 100)
  print("")
cat("Mailslots (JNI only) mean in MICRO-SECONDS (40 00 bytes)", MAILSLOTS_JNI_4000_BYTES / 100)
  print("")
cat("Mailslots (JNI only) mean in MICRO-SECONDS (40 000 bytes)", MAILSLOTS_JNI_40000_BYTES / 100)
  print("")

print("===========================================================")

# MAILSLOTS (JAVA IO READ)
# =================================================================

MAILSLOTS_JAVAIO_40_BYTES <- 212842.3
MAILSLOTS_JAVAIO_400_BYTES <- 221904.3
MAILSLOTS_JAVAIO_4000_BYTES <- 224207.5
MAILSLOTS_JAVAIO_40000_BYTES <- 232136.6
  # IN NS
cat("Mailslots (Java IO read) mean in NS (40 BYTES)", MAILSLOTS_JAVAIO_40_BYTES)
  print("")
cat("Mailslots (Java IO read) mean in NS (400 BYTES)", MAILSLOTS_JAVAIO_400_BYTES)
  print("")
cat("Mailslots (Java IO read) mean in NS (4 000 0BYTES)", MAILSLOTS_JAVAIO_4000_BYTES)
  print("")
cat("Mailslots (Java IO read) mean in NS (40 000 BYTES)", MAILSLOTS_JAVAIO_40000_BYTES)
  print("")
  #IN MICRO-SECONDS
cat("Mailslots (Java IO read) mean in MICRO-SECONDS (40 BYTES)", MAILSLOTS_JAVAIO_40_BYTES / 100)
  print("")
cat("Mailslots (Java IO read) mean in MICRO-SECONDS (400 BYTES)", MAILSLOTS_JAVAIO_400_BYTES / 100)
  print("")
cat("Mailslots (Java IO read) mean in MICRO-SECONDS (4 000 0BYTES)", MAILSLOTS_JAVAIO_4000_BYTES / 100)
  print("")
cat("Mailslots (Java IO read) mean in MICRO-SECONDS (40 000 BYTES)", MAILSLOTS_JAVAIO_40000_BYTES / 100)
  print("")
print("===========================================================")

# WINSOCK
# =================================================================
  # DATA
WINSOCK_40_BYTES <- 2661302.3
WINSOCK_400_BYTES <- 2907674
WINSOCK_4000_BYTES <- 3105262.2
WINSOCK_40000_BYTES <- 39800788.5
  # IN NS
cat("Winsock mean in NS (40 BYTES): ", WINSOCK_40_BYTES)
  print("")
cat("Winsock mean in NS (400 BYTES): ", WINSOCK_400_BYTES)
  print("")
cat("Winsock mean in NS (4 000 BYTES): ", WINSOCK_4000_BYTES)
  print("")
cat("Winsock mean in NS (40 000 BYTES): ", WINSOCK_40000_BYTES)
  print("")
  # IN MICRO-SECONDS
cat("Winsock mean in MICRO-SECONDS (40 BYTES): ", WINSOCK_40_BYTES / 100)
  print("")
cat("Winsock mean in MICRO-SECONDS (400 BYTES): ", WINSOCK_400_BYTES / 100)
  print("")
cat("Winsock mean in MICRO-SECONDS (4 000 BYTES): ", WINSOCK_4000_BYTES / 100)
  print("")
cat("Winsock mean in MICRO-SECONDS (40 000 BYTES): ", WINSOCK_40000_BYTES / 100)
  print("")
print("===========================================================")

# JAVA SOCKETS
# =================================================================
  # DATA
JSOCKETS_40_BYTES <- 6039177.6
JSOCKETS_400_BYTES <- 6016447.6
JSOCKETS_4000_BYTES <- 6217622.3
JSOCKETS_40000_BYTES <- 6285624.7
  # IN NS
cat("Java Sockets mean in NS (40 BYTES): ", JSOCKETS_40_BYTES)
  print("")
cat("Java Sockets mean in NS (400 BYTES): ", JSOCKETS_400_BYTES)
  print("")
cat("Java Sockets mean in NS (4 000 BYTES): ", JSOCKETS_4000_BYTES)
  print("")
cat("Java Sockets mean in NS (40 000 BYTES): ", JSOCKETS_40000_BYTES)
  print("")
  # IN micro-seconds
cat("Java Sockets mean in MICRO-SECONDS (40 BYTES): ", JSOCKETS_40_BYTES / 100)
  print("")
cat("Java Sockets mean in MICRO-SECONDS (400 BYTES): ", JSOCKETS_400_BYTES / 100)
  print("")
cat("Java Sockets mean in MICRO-SECONDS (4 000 BYTES): ", JSOCKETS_4000_BYTES / 100)
  print("")
cat("Java Sockets mean in MICRO-SECONDS (40 000 BYTES): ", JSOCKETS_40000_BYTES / 100)
  print("")
print("===========================================================")

# MEMORY MAPPING
# =================================================================
  # DATA
MEM_MAP_40_BYTES <- 84653.6
MEM_MAP_400_BYTES <- 86239.4
MEM_MAP_4000_BYTES <- 95339.1
MEM_MAP_40000_BYTES <- 119353.3
  # IN NS
cat("Mem Map mean in NS (40 Bytes): ", MEM_MAP_40_BYTES)
  print("")
cat("Mem Map mean in NS (400 Bytes): ", MEM_MAP_400_BYTES)
  print("")
cat("Mem Map mean in NS (4 000 Bytes): ", MEM_MAP_4000_BYTES)
  print("")
cat("Mem Map mean in NS (40 000 Bytes): ", MEM_MAP_40000_BYTES)
  print("")
 # IN MICRO-SECONDS
cat("Mem Map mean in MICRO-SECONDS (40 Bytes): ", MEM_MAP_40_BYTES / 100)
  print("")
cat("Mem Map mean in MICRO-SECONDS (400 Bytes): ", MEM_MAP_400_BYTES / 100)
  print("")
cat("Mem Map mean in MICRO-SECONDS (4 000 Bytes): ", MEM_MAP_4000_BYTES / 100)
  print("")
cat("Mem Map mean in MICRO-SECONDS (40 000 Bytes): ", MEM_MAP_40000_BYTES / 100)
  print("")
print("===========================================================")

# DATA COPY
# =================================================================
  # DATA IN MS
DC_40_BYTES <- 2223.011622
DC_400_BYTES <- 1977.594735
DC_4000_BYTES <- 1973.904978
DC_40000_BYTES <- 2523.616091

cat("Data Copy mean in milliseconds (40 Bytes): ", DC_40_BYTES)
  print("")
cat("Data Copy mean in milliseconds (400 Bytes): ", DC_400_BYTES)
  print("")
cat("Data Copy mean in milliseconds (4 000 Bytes): ", DC_4000_BYTES)
  print("")
cat("Data Copy mean in milliseconds (40 000 Bytes): ", DC_40000_BYTES)
  print("")
print("===========================================================")

# GRAPH GEN
# =============================================================

# 1) JAVA SOCKETS LINE GRAPH

JS_VECTOR <- c(
JSOCKETS_40_BYTES / 100,
JSOCKETS_400_BYTES / 100,
JSOCKETS_4000_BYTES / 100,
JSOCKETS_40000_BYTES / 100
)
png(file = "JavaSocketsLineChart.png")
plot(
  JS_VECTOR,
  type="o",
  col="red",
  xlab="Byte size",
  ylab="time in micro-seconds",
  main="Java Sockets Performance",
)
axis(1, at=1:4, lab=c("40","400","4 000","40 000"))
dev.off()
# Some graph gen
  # 1) bar plot without Data Copy
#    options(scipen=8)
#    data <- c(
#    npMean,
#    msJNIMean,
#    msJavaIOReadMean,
#    (winsockMean / 2),
#    (javaSocketsMean / 2),
#    memMapMean
#    )
#    png(file = "resultsInNS.png")
#    barplot(
#      data,
  #    main="40 Byte Message Sent",
  #    xlab="Windows IPC mechanims",
    #  ylab="Time in NS",
  #    ylim=c(0, 3500000),
  #    space=3,
  #    names.arg=c("NP", "MS (JNI)", "MS (IO)", "Winsock", "Java Socks", "Mem Map")
#    )
#    dev.off()
