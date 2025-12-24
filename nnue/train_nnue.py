import struct, torch, random
import torch.nn as nn

from tqdm import tqdm


FEATURES = 768
HIDDEN = 256
EPOCHS = 4
LR = 1e-3

class NNUE(nn.Module):
    def __init__(self):
        super().__init__()
        self.w1 = nn.Embedding(FEATURES, HIDDEN)
        self.b1 = nn.Parameter(torch.zeros(HIDDEN))
        self.w2 = nn.Linear(HIDDEN, 1)

    def forward(self, feats):
        x = self.b1.clone()
        x += self.w1(feats).sum(0)
        x = torch.clamp(x, 0, 127)
        return self.w2(x)

model = NNUE()
opt = torch.optim.Adam(model.parameters(), lr=LR)

def dataset(path):
    with open(path, "rb") as f:
        while True:
            n = f.read(1)
            if not n:
                break
            n = struct.unpack("B", n)[0]
            feats = struct.unpack(f"{n}H", f.read(2*n))
            cp = struct.unpack("h", f.read(2))[0]
            yield feats, cp / 100.0

from tqdm import tqdm

for epoch in range(EPOCHS):
    total_loss = 0.0
    count = 0

    pbar = tqdm(
        dataset("data.bin"),
        desc=f"Epoch {epoch+1}/{EPOCHS}",
        unit="pos"
    )

    for feats, target in pbar:
        feats = torch.tensor(feats, dtype=torch.long)
        target = torch.tensor([target])

        pred = model(feats)
        loss = (pred - target).pow(2).mean()

        opt.zero_grad()
        loss.backward()
        opt.step()

        total_loss += loss.item()
        count += 1

        if count % 1000 == 0:
            pbar.set_postfix(loss=total_loss / count)

    print(f"Epoch {epoch+1} final loss = {total_loss / count:.4f}")


torch.save(model.state_dict(), "nnue.pt")
