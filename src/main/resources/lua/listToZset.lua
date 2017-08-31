---
--- Created by Pengjinfei.
--- DateTime: 8/31/17 20:58
---
local lkey=KEYS[1]
local zkey=KEYS[2]
local score=KEYS[3]

while(true) do
    local val=redis.call("rpop",lkey)
    if(val ~= false) then
        redis.call("zadd",zkey,tonumber(score),val)
    else
        break;
    end
end
return true;