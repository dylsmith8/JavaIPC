# Author: Dylan Smith
# Date: 25 June 2016
# R script that generates stats and graphs based on IPC results

# Named pipes:
NAMED_PIPES_DATA <- c(
  1077617,
  551269,
  780460,
  1119150,
  1043634,
  926584,
  1069687,
  1432920,
  770265,
  917145
)

npMean <- mean(NAMED_PIPES_DATA)
print("Named pipe mean in NS:")
print(npMean)
print("Named pipe mean in micro-seconds:")
print(npMean / 100)

# Mailslots: Times for JNI read/write
MAILSLOT_JNI_DATA <- c(
  887315,
  864283,
  831811,
  1107823,
  973781,
  899020,
  819351,
  844649,
  1049675,
  674737
)

msJNIMean <- mean(MAILSLOT_JNI_DATA)
print("Mailslot JNI mean in NS:")
print(msJNIMean)
print("Mailslot JNI mean in micro-seconds:")
print(msJNIMean/100)

# Mailslots: Java IO read
MAILSLOT_JAVA_IO_DATA <- c(
  131637,
  142792,
  135653,
  172244,
  130744,
  81214,
  136992,
  123604,
  134760,
  138331
)

msJavaIOReadMean <- mean(MAILSLOT_JAVA_IO_DATA)
print("Mailslot Java IO Read mean NS:")
print(msJavaIOReadMean)
print("Mailslot Java IO Read in micro-seconds")
print(msJavaIOReadMean / 100)

# Winsock
WINSOCK_DATA <- c(
  2504495,
  2651374,
  2703858,
  3002902,
  2988554,
  2392731,
  2601534,
  2953061,
  2869993,
  2567551
)

winsockMean <- mean(WINSOCK_DATA)
print("Winsock Mean in NS:")
print(winsockMean / 2)
print("Winsock mean in micro-seconds")
print((winsockMean / 2) / 100)

# Java Sockets
JAVA_SOCKETS_DATA <- c(
  6751526,
  5226476,
  5336353,
  5476813,
  5819657,
  5788695,
  5524766,
  8326795,
  6031479,
  5402429
)

javaSocketsMean <- mean(JAVA_SOCKETS_DATA)
print("Java Sockets Mean in NS:")
print(javaSocketsMean / 2)
print("Java Sockets mean in micro-seconds")
print((javaSocketsMean / 2) / 100)

# Memory mapping without synchronisation
MEM_MAP_DATA <- c(
  90241,
  75516,
  80047,
  71363,
  74006,
  107610,
  114407,
  72873,
  89487,
  74383
)

memMapMean <- mean(MEM_MAP_DATA)
print("Memory Map mean without sync in NS:")
print(memMapMean)
print("Memory Map mean without sync in micro-seconds")
print(memMapMean / 100)

# Data Copy
DATACOPY_DATA <- c(
  1531657528,
  1886643750,
  2850938176,
  2932208342,
  2216775322,
  3080610911,
  3229107120,
  2996263458,
  2272719241,
  1559322136
)

dataCopyMean <- mean(DATACOPY_DATA)
print("Data Copy mean in NS:")
print(dataCopyMean)
print("Data Copy mean in micro-seconds")
print(dataCopyMean / 100)

# Some graph gen
  # 1) bar plot without Data Copy
    options(scipen=8)
    data <- c(
    npMean,
    msJNIMean,
    msJavaIOReadMean,
    (winsockMean / 2),
    (javaSocketsMean / 2),
    memMapMean
    )
    png(file = "resultsInNS.png")
    barplot(
      data,
      main="40 Byte Message Sent",
      xlab="Windows IPC mechanims",
      ylab="Time in NS",
      ylim=c(0, 3500000),
      space=3,
      names.arg=c("NP", "MS (JNI)", "MS (IO)", "Winsock", "Java Socks", "Mem Map")
    )
    dev.off()
