ids, txts = [], []
with open("gametexts.txt", "r") as f:
    for line in f:
        id = int(line.split("{")[1].split(",")[0])
        txt = line.split("\"")[1]
        print(id, txt)
        ids.append(id)
        txts.append(txt)


with open("gametexts.xml", "w") as f:
    print("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n\t<integer-array name=\"gametexts_idxs\">", file = f)
    for id in ids:
        print("\t\t<item>" + str(id) + "</item>", file = f)
    print("\t</integer-array>\n\t<string-array name=\"gametexts_strings\">", file = f)
    for txt in txts:
        print("\t\t<item>" + txt + "</item>", file = f)
    print("\t</string-array>\n</resources>", file = f)