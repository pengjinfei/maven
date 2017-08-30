---
--- Created by Pengjinfei.
--- DateTime: 8/30/17 22:50
---
local origin=KEYS[1]
local dest=KEYS[2]

while(true) do
    local num = redis.call('rpoplpush', origin, dest)
    if(num ~= 1) then break end;
end
return true;