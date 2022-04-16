from pydub import AudioSegment
from os import listdir
from os.path import isfile, join
import sys

def get_silence(audio, threshold, interval):
    "get length of silence in seconds from a wav file"

    # swap out pydub import for other types of audio
    song = AudioSegment.from_mp3(audio)

    # break into chunks
    chunks = [song[i:i+interval] for i in range(0, len(song), interval)]

    # find number of chunks with dBFS below threshold
    silent_blocks = 0
    for c in chunks:
        if c.dBFS == float('-inf') or c.dBFS < threshold:
            silent_blocks += 1
        else:
            break

    # convert blocks into seconds
    return round(silent_blocks * (interval/1000), 3)

# get files in a directory
audio_path = sys.argv[1]

threshold = -35 # tweak based on signal-to-noise ratio

interval = 10 # ms, increase to speed up

print(get_silence(audio_path, threshold, interval))
