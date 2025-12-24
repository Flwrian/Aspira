import torch, struct

FEATURES = 768
HIDDEN = 256
SCALE = 64

state = torch.load("nnue.pt")

w1 = (state["w1.weight"] * SCALE).round().short()
b1 = (state["b1"] * SCALE).round().int()
w2 = (state["w2.weight"][0] * SCALE).round().short()
b2 = int((state["w2.bias"][0] * SCALE).round())

# print model shape
print("w1:", w1.shape)
print("b1:", b1.shape)
print("w2:", w2.shape)
print("b2:", b2)


with open("nnue.nnue", "wb") as f:
    f.write(struct.pack(">i", HIDDEN))
    f.write(struct.pack(">i", FEATURES))

    for fidx in range(FEATURES):
        for i in range(HIDDEN):
            f.write(struct.pack(">h", w1[fidx][i].item()))

    for i in range(HIDDEN):
        f.write(struct.pack(">i", b1[i].item()))

    for i in range(HIDDEN):
        f.write(struct.pack(">h", w2[i].item()))

    f.write(struct.pack(">i", b2))

print("Exported nnue.nnue")
