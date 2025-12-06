drop table if exists messages;

drop table if exists conversation_members;

drop table if exists conversations;

drop table if exists friendships;

drop table if exists login_history;

drop table if exists users;

-- USERS
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    display_name VARCHAR(255),
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    admin BOOLEAN DEFAULT FALSE,
    is_online BOOLEAN DEFAULT FALSE,
    address TEXT,
    birthday DATE,
    gender BOOLEAN,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

create table friendships (
  id UUID primary key default gen_random_uuid (),
  user_id UUID not null references users (id) on delete CASCADE,
  friend_id UUID not null references users (id) on delete CASCADE,
  status TEXT not null check (
    status in ('pending', 'accepted', 'blocked', 'rejected')
  ), -- trạng thái
  requester_id UUID not null references users (id), -- ai gửi request (useful when status = 'pending' or 'rejected')
  created_at TIMESTAMPTZ default now(),
  updated_at TIMESTAMPTZ default now(),
  accepted_at TIMESTAMPTZ, -- nếu accepted thì thời điểm
  check (user_id <> friend_id)
);

-- CONVERSATIONS (room)
create table conversations (
  id UUID primary key,
  isGroup boolean not null,
  title TEXT, -- optional for groups
  created_by UUID references users (id),
  created_at TIMESTAMPTZ default now()
);

-- MEMBERS (who's in which conversation)
create table conversation_members (
  conversation_id UUID references conversations (id) on delete CASCADE,
  user_id UUID references users (id) on delete CASCADE,
  role TEXT, -- admin/member
  joined_at TIMESTAMPTZ default now(),
  last_read_message_id UUID, -- optional fast unread calc
  last_read_at TIMESTAMPTZ,
  primary key (conversation_id, user_id)
);

-- MESSAGES
create table messages (
  id UUID primary key,
  conversation_id UUID references conversations (id) not null,
  sender_id UUID references users (id) not null,
  -- optional monotonic seq per conversation for ordering
  conversation_seq BIGINT, -- ensure unique per conversation
  client_message_id TEXT, -- idempotency key from client
  content TEXT,
  created_at TIMESTAMPTZ default now(),
  reply_to_message_id UUID references messages (id),
  is_deleted BOOLEAN default false,
  check (
    conversation_seq is null
    or conversation_seq >= 0
  )
);

create table public.login_history (
  id uuid not null default gen_random_uuid (),
  created_at timestamp with time zone not null default now(),
  user_id uuid null,
  time timestamp with time zone null default now(),
  constraint login_history_pkey primary key (id),
  constraint login_history_user_id_fkey foreign KEY (user_id) references users (id) on delete CASCADE
) TABLESPACE pg_default;

-- SAMPLE DATA FOR CHAT SCHEMA
-- NOTE: replace the password placeholders with real password hashes in production.
-- 1) USERS
insert into
  users (
    id,
    username,
    display_name,
    email,
    admin,
    password,
    created_at
  )
values
  (
    '11111111-1111-1111-1111-111111111111',
    'user',
    'User test',
    'user@example.com',
    false,
    '123',
    '2025-10-20 08:30:00+07'
  ),
  (
    '22222222-2222-2222-2222-222222222222',
    'bob',
    'Bob Tran',
    'bob@example.com',
    false,
    'HASHED_PW_BOB',
    '2025-10-21 09:10:00+07'
  ),
  (
    '33333333-3333-3333-3333-333333333333',
    'admin',
    'Admin test',
    'admin@example.com',
    true,
    '123',
    '2025-09-15 11:00:00+07'
  ),
  (
    '44444444-4444-4444-4444-444444444444',
    'dave',
    'Dave Pham',
    'dave@example.com',
    false,
    'HASHED_PW_DAVE',
    '2025-10-01 14:20:00+07'
  ),
  (
    '55555555-5555-5555-5555-555555555555',
    'eve',
    'Eve Hoang',
    'eve@example.com',
    false,
    'HASHED_PW_EVE',
    '2025-10-05 16:45:00+07'
  ),
  (
    '66666666-6666-6666-6666-666666666666',
    'frank',
    'Frank Vo',
    'frank@example.com',
    false,
    'HASHED_PW_FRANK',
    '2025-09-25 10:00:00+07'
  ),
  (
    '77777777-7777-7777-7777-777777777777',
    'grace',
    'Grace Nguyen',
    'grace@example.com',
    false,
    'HASHED_PW_GRACE',
    '2025-09-26 12:00:00+07'
  ),
  (
    '88888888-8888-8888-8888-888888888888',
    'heidi',
    'Heidi Bui',
    'heidi@example.com',
    false,
    'HASHED_PW_HEIDI',
    '2025-10-10 09:30:00+07'
  ),
  (
    '99999999-9999-9999-9999-999999999999',
    'ivan',
    'Ivan Do',
    'ivan@example.com',
    false,
    'HASHED_PW_IVAN',
    '2025-10-11 08:00:00+07'
  ),
  (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'judy',
    'Judy Kim',
    'judy@example.com',
    false,
    'HASHED_PW_JUDY',
    '2025-08-20 07:45:00+07'
  ),
  (
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    'khanh',
    'Khanh Nguyen',
    'khanh@example.com',
    false,
    'HASHED_PW_KHANH',
    '2025-07-30 13:00:00+07'
  ),
  (
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    'leo',
    'Leo Tran',
    'leo@example.com',
    false,
    'HASHED_PW_LEO',
    '2025-09-01 18:20:00+07'
  );

-- 2) FRIENDSHIPS (various statuses)
-- accepted between alice & bob
insert into
  friendships (
    id,
    user_id,
    friend_id,
    status,
    requester_id,
    created_at,
    updated_at,
    accepted_at
  )
values
  (
    'f1111111-0000-0000-0000-000000000001',
    '11111111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222',
    'accepted',
    '11111111-1111-1111-1111-111111111111',
    '2025-10-21 09:15:00+07',
    '2025-10-21 09:20:00+07',
    '2025-10-21 09:20:00+07'
  );

-- pending: carol sent request to alice
insert into
  friendships (
    id,
    user_id,
    friend_id,
    status,
    requester_id,
    created_at,
    updated_at
  )
values
  (
    'f2222222-0000-0000-0000-000000000002',
    '33333333-3333-3333-3333-333333333333',
    '11111111-1111-1111-1111-111111111111',
    'pending',
    '33333333-3333-3333-3333-333333333333',
    '2025-11-01 10:00:00+07',
    '2025-11-01 10:00:00+07'
  );

-- blocked: eve blocked dave (status 'blocked', requester_id set to dave who attempted to add)
insert into
  friendships (
    id,
    user_id,
    friend_id,
    status,
    requester_id,
    created_at,
    updated_at
  )
values
  (
    'f3333333-0000-0000-0000-000000000003',
    '44444444-4444-4444-4444-444444444444',
    '55555555-5555-5555-5555-555555555555',
    'blocked',
    '44444444-4444-4444-4444-444444444444',
    '2025-10-06 12:00:00+07',
    '2025-10-06 12:05:00+07'
  );

-- accepted: frank <-> grace
insert into
  friendships (
    id,
    user_id,
    friend_id,
    status,
    requester_id,
    created_at,
    updated_at,
    accepted_at
  )
values
  (
    'f4444444-0000-0000-0000-000000000004',
    '66666666-6666-6666-6666-666666666666',
    '77777777-7777-7777-7777-777777777777',
    'accepted',
    '66666666-6666-6666-6666-666666666666',
    '2025-09-27 09:00:00+07',
    '2025-09-27 09:05:00+07',
    '2025-09-27 09:05:00+07'
  );

-- rejected: heidi rejected ivan's request
insert into
  friendships (
    id,
    user_id,
    friend_id,
    status,
    requester_id,
    created_at,
    updated_at
  )
values
  (
    'f5555555-0000-0000-0000-000000000005',
    '88888888-8888-8888-8888-888888888888',
    '99999999-9999-9999-9999-999999999999',
    'rejected',
    '99999999-9999-9999-9999-999999999999',
    '2025-10-12 08:10:00+07',
    '2025-10-12 08:12:00+07'
  );

-- accepted: bob <-> frank
insert into
  friendships (
    id,
    user_id,
    friend_id,
    status,
    requester_id,
    created_at,
    updated_at,
    accepted_at
  )
values
  (
    'f6666666-0000-0000-0000-000000000006',
    '22222222-2222-2222-2222-222222222222',
    '66666666-6666-6666-6666-666666666666',
    'accepted',
    '22222222-2222-2222-2222-222222222222',
    '2025-10-22 10:00:00+07',
    '2025-10-22 10:02:00+07',
    '2025-10-22 10:02:00+07'
  );

-- accepted: alice <-> ivan
insert into
  friendships (
    id,
    user_id,
    friend_id,
    status,
    requester_id,
    created_at,
    updated_at,
    accepted_at
  )
values
  (
    'f7777777-0000-0000-0000-000000000007',
    '11111111-1111-1111-1111-111111111111',
    '99999999-9999-9999-9999-999999999999',
    'accepted',
    '99999999-9999-9999-9999-999999999999',
    '2025-10-11 09:00:00+07',
    '2025-10-11 09:05:00+07',
    '2025-10-11 09:05:00+07'
  );

-- pending: carol -> grace
insert into
  friendships (
    id,
    user_id,
    friend_id,
    status,
    requester_id,
    created_at,
    updated_at
  )
values
  (
    'f8888888-0000-0000-0000-000000000008',
    '33333333-3333-3333-3333-333333333333',
    '77777777-7777-7777-7777-777777777777',
    'pending',
    '33333333-3333-3333-3333-333333333333',
    '2025-11-02 15:00:00+07',
    '2025-11-02 15:00:00+07'
  );

-- additional varied friendships for richer graph
insert into
  friendships (
    id,
    user_id,
    friend_id,
    status,
    requester_id,
    created_at,
    updated_at,
    accepted_at
  )
values
  (
    'f9999999-0000-0000-0000-000000000009',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    'accepted',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '2025-08-21 08:00:00+07',
    '2025-08-21 08:05:00+07',
    '2025-08-21 08:05:00+07'
  ),
  (
    'faaaaaaa-0000-0000-0000-00000000000a',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    'accepted',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '2025-09-02 09:00:00+07',
    '2025-09-02 09:05:00+07',
    '2025-09-02 09:05:00+07'
  );

-- 3) CONVERSATIONS
-- DM: alice <-> bob
insert into
  conversations (id, isGroup, title, created_by, created_at)
values
  (
    'c1111111-0000-0000-0000-000000000001',
    false,
    null,
    '11111111-1111-1111-1111-111111111111',
    '2025-10-21 09:00:00+07'
  );

-- DM: alice <-> ivan
insert into
  conversations (id, isGroup, title, created_by, created_at)
values
  (
    'c2222222-0000-0000-0000-000000000002',
    false,
    null,
    '11111111-1111-1111-1111-111111111111',
    '2025-10-11 08:05:00+07'
  );

-- Group: Study Group (carol created)
insert into
  conversations (id, isGroup, title, created_by, created_at)
values
  (
    'c3333333-0000-0000-0000-000000000003',
    true,
    'Study Group',
    '33333333-3333-3333-3333-333333333333',
    '2025-09-30 11:00:00+07'
  );

-- Group: Project Team (dave created)
insert into
  conversations (id, isGroup, title, created_by, created_at)
values
  (
    'c4444444-0000-0000-0000-000000000004',
    true,
    'Project: Apollo',
    '44444444-4444-4444-4444-444444444444',
    '2025-10-01 14:30:00+07'
  );

-- Group: Random Chat (heidi created)
insert into
  conversations (id, isGroup, title, created_by, created_at)
values
  (
    'c5555555-0000-0000-0000-000000000005',
    true,
    'Random',
    '88888888-8888-8888-8888-888888888888',
    '2025-10-10 10:00:00+07'
  );

-- 4) CONVERSATION MEMBERS
-- DM alice-bob
insert into
  conversation_members (conversation_id, user_id, role, joined_at)
values
  (
    'c1111111-0000-0000-0000-000000000001',
    '11111111-1111-1111-1111-111111111111',
    'member',
    '2025-10-21 09:00:00+07'
  ),
  (
    'c1111111-0000-0000-0000-000000000001',
    '22222222-2222-2222-2222-222222222222',
    'member',
    '2025-10-21 09:00:10+07'
  );

-- DM alice-ivan
insert into
  conversation_members (conversation_id, user_id, role, joined_at)
values
  (
    'c2222222-0000-0000-0000-000000000002',
    '11111111-1111-1111-1111-111111111111',
    'member',
    '2025-10-11 08:05:00+07'
  ),
  (
    'c2222222-0000-0000-0000-000000000002',
    '99999999-9999-9999-9999-999999999999',
    'member',
    '2025-10-11 08:05:10+07'
  );

-- Study Group members: carol(admin), dave, eve, alice
insert into
  conversation_members (conversation_id, user_id, role, joined_at)
values
  (
    'c3333333-0000-0000-0000-000000000003',
    '33333333-3333-3333-3333-333333333333',
    'admin',
    '2025-09-30 11:00:00+07'
  ),
  (
    'c3333333-0000-0000-0000-000000000003',
    '44444444-4444-4444-4444-444444444444',
    'member',
    '2025-09-30 11:05:00+07'
  ),
  (
    'c3333333-0000-0000-0000-000000000003',
    '55555555-5555-5555-5555-555555555555',
    'member',
    '2025-09-30 11:10:00+07'
  ),
  (
    'c3333333-0000-0000-0000-000000000003',
    '11111111-1111-1111-1111-111111111111',
    'member',
    '2025-09-30 11:20:00+07'
  );

-- Project Team members: dave(admin), frank, grace, khanh
insert into
  conversation_members (conversation_id, user_id, role, joined_at)
values
  (
    'c4444444-0000-0000-0000-000000000004',
    '44444444-4444-4444-4444-444444444444',
    'admin',
    '2025-10-01 14:30:00+07'
  ),
  (
    'c4444444-0000-0000-0000-000000000004',
    '66666666-6666-6666-6666-666666666666',
    'member',
    '2025-10-01 14:35:00+07'
  ),
  (
    'c4444444-0000-0000-0000-000000000004',
    '77777777-7777-7777-7777-777777777777',
    'member',
    '2025-10-01 14:36:00+07'
  ),
  (
    'c4444444-0000-0000-0000-000000000004',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    'member',
    '2025-10-01 14:40:00+07'
  );

-- Random Chat: heidi(admin), judy, leo
insert into
  conversation_members (conversation_id, user_id, role, joined_at)
values
  (
    'c5555555-0000-0000-0000-000000000005',
    '88888888-8888-8888-8888-888888888888',
    'admin',
    '2025-10-10 10:00:00+07'
  ),
  (
    'c5555555-0000-0000-0000-000000000005',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'member',
    '2025-10-10 10:05:00+07'
  ),
  (
    'c5555555-0000-0000-0000-000000000005',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    'member',
    '2025-10-10 10:06:00+07'
  );

-- 5) MESSAGES
-- DM alice <-> bob (conversation_seq ascending)
-- Alice -> Bob (seq 1)
insert into
  messages (
    id,
    conversation_id,
    sender_id,
    conversation_seq,
    client_message_id,
    content,
    created_at,
    is_deleted
  )
values
  (
    '11111111-0000-0000-0000-000000000011',
    'c1111111-0000-0000-0000-000000000001',
    '11111111-1111-1111-1111-111111111111',
    1,
    'cm-a-1',
    'Hey Bob, you around?',
    '2025-10-21 09:01:00+07',
    false
  );

-- Bob reply (seq 2)
insert into
  messages (
    id,
    conversation_id,
    sender_id,
    conversation_seq,
    client_message_id,
    content,
    created_at,
    reply_to_message_id
  )
values
  (
    '11111111-0000-0000-0000-000000000012',
    'c1111111-0000-0000-0000-000000000001',
    '22222222-2222-2222-2222-222222222222',
    2,
    'cm-b-1',
    'Hi Alice — yes, free now.',
    '2025-10-21 09:01:15+07',
    '11111111-0000-0000-0000-000000000011'
  );

-- Alice (seq 3)
insert into
  messages (
    id,
    conversation_id,
    sender_id,
    conversation_seq,
    client_message_id,
    content,
    created_at
  )
values
  (
    '11111111-0000-0000-0000-000000000013',
    'c1111111-0000-0000-0000-000000000001',
    '11111111-1111-1111-1111-111111111111',
    3,
    'cm-a-2',
    'Great — can you review the doc I shared?',
    '2025-10-21 09:02:00+07'
  );

-- Bob (seq 4) deleted message example
insert into
  messages (
    id,
    conversation_id,
    sender_id,
    conversation_seq,
    client_message_id,
    content,
    created_at,
    is_deleted
  )
values
  (
    '11111111-0000-0000-0000-000000000014',
    'c1111111-0000-0000-0000-000000000001',
    '22222222-2222-2222-2222-222222222222',
    4,
    'cm-b-2',
    'Oops typo, ignore that',
    '2025-10-21 09:03:00+07',
    true
  );

-- DM alice <-> ivan
insert into
  messages (
    id,
    conversation_id,
    sender_id,
    conversation_seq,
    client_message_id,
    content,
    created_at
  )
values
  (
    '22222222-0000-0000-0000-000000000021',
    'c2222222-0000-0000-0000-000000000002',
    '11111111-1111-1111-1111-111111111111',
    1,
    'cm-a-iv-1',
    'Hi Ivan — quick question about the API.',
    '2025-10-11 08:06:00+07'
  ),
  (
    '22222222-0000-0000-0000-000000000022',
    'c2222222-0000-0000-0000-000000000002',
    '99999999-9999-9999-9999-999999999999',
    2,
    'cm-iv-1',
    'Sure, what do you need?',
    '2025-10-11 08:06:20+07'
  ),
  (
    '22222222-0000-0000-0000-000000000023',
    'c2222222-0000-0000-0000-000000000002',
    '11111111-1111-1111-1111-111111111111',
    3,
    'cm-a-iv-2',
    'How to handle pagination cursor?',
    '2025-10-11 08:07:00+07'
  );

-- Study Group messages
insert into
  messages (
    id,
    conversation_id,
    sender_id,
    conversation_seq,
    client_message_id,
    content,
    created_at
  )
values
  (
    '33333333-0000-0000-0000-000000000031',
    'c3333333-0000-0000-0000-000000000003',
    '33333333-3333-3333-3333-333333333333',
    1,
    'cm-c-1',
    'Welcome everyone! Study meeting tonight at 7pm.',
    '2025-09-30 11:01:00+07'
  ),
  (
    '33333333-0000-0000-0000-000000000032',
    'c3333333-0000-0000-0000-000000000003',
    '44444444-4444-4444-4444-444444444444',
    2,
    'cm-d-1',
    'I can prepare slides for the intro.',
    '2025-09-30 11:05:30+07'
  ),
  (
    '33333333-0000-0000-0000-000000000033',
    'c3333333-0000-0000-0000-000000000003',
    '55555555-5555-5555-5555-555555555555',
    3,
    'cm-e-1',
    'I will cover the examples section.',
    '2025-09-30 11:07:00+07'
  ),
  (
    '33333333-0000-0000-0000-000000000034',
    'c3333333-0000-0000-0000-000000000003',
    '11111111-1111-1111-1111-111111111111',
    4,
    'cm-a-3',
    'I can proofread slides after they are ready.',
    '2025-09-30 11:10:00+07'
  ),
  (
    '33333333-0000-0000-0000-000000000035',
    'c3333333-0000-0000-0000-000000000003',
    '33333333-3333-3333-3333-333333333333',
    5,
    'cm-c-2',
    'Thanks Alice! That helps a lot.',
    '2025-09-30 11:12:00+07'
  );

-- Project Team messages (with more activity)
insert into
  messages (
    id,
    conversation_id,
    sender_id,
    conversation_seq,
    client_message_id,
    content,
    created_at
  )
values
  (
    '44444444-0000-0000-0000-000000000041',
    'c4444444-0000-0000-0000-000000000004',
    '44444444-4444-4444-4444-444444444444',
    1,
    'cm-dp-1',
    'Kickoff meeting tomorrow 10am.',
    '2025-10-01 14:31:00+07'
  ),
  (
    '44444444-0000-0000-0000-000000000042',
    'c4444444-0000-0000-0000-000000000004',
    '66666666-6666-6666-6666-666666666666',
    2,
    'cm-fp-1',
    'I''ll prepare the architecture draft.',
    '2025-10-01 14:35:00+07'
  ),
  (
    '44444444-0000-0000-0000-000000000043',
    'c4444444-0000-0000-0000-000000000004',
    '77777777-7777-7777-7777-777777777777',
    3,
    'cm-gp-1',
    'Can someone own the CI setup?',
    '2025-10-01 14:36:30+07'
  ),
  (
    '44444444-0000-0000-0000-000000000044',
    'c4444444-0000-0000-0000-000000000004',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    4,
    'cm-kp-1',
    'I can take CI tasks this week.',
    '2025-10-01 14:40:00+07'
  );

-- Random chat messages
insert into
  messages (
    id,
    conversation_id,
    sender_id,
    conversation_seq,
    client_message_id,
    content,
    created_at
  )
values
  (
    '55555555-0000-0000-0000-000000000051',
    'c5555555-0000-0000-0000-000000000005',
    '88888888-8888-8888-8888-888888888888',
    1,
    'cm-h-1',
    'Good morning! Anyone up for coffee?',
    '2025-10-10 10:01:00+07'
  ),
  (
    '55555555-0000-0000-0000-000000000052',
    'c5555555-0000-0000-0000-000000000005',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    2,
    'cm-judy-1',
    'I''m in — let''s meet at 11.',
    '2025-10-10 10:05:30+07'
  ),
  (
    '55555555-0000-0000-0000-000000000053',
    'c5555555-0000-0000-0000-000000000005',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    3,
    'cm-leo-1',
    'Count me in!',
    '2025-10-10 10:06:00+07'
  );

-- A reply example where message replies to another in same conversation (Project Team)
insert into
  messages (
    id,
    conversation_id,
    sender_id,
    conversation_seq,
    client_message_id,
    content,
    created_at,
    reply_to_message_id
  )
values
  (
    '44444444-0000-0000-0000-000000000045',
    'c4444444-0000-0000-0000-000000000004',
    '44444444-4444-4444-4444-444444444444',
    5,
    'cm-dp-2',
    'Thanks — I''ll assign CI tasks.',
    '2025-10-01 14:45:00+07',
    '44444444-0000-0000-0000-000000000044'
  );

-- More varied messages across different times to create history
insert into
  messages (
    id,
    conversation_id,
    sender_id,
    conversation_seq,
    client_message_id,
    content,
    created_at
  )
values
  (
    '66666666-0000-0000-0000-000000000061',
    'c3333333-0000-0000-0000-000000000003',
    '44444444-4444-4444-4444-444444444444',
    6,
    'cm-d-2',
    'Reminder: bring laptops.',
    '2025-09-30 12:00:00+07'
  ),
  (
    '77777777-0000-0000-0000-000000000071',
    'c3333333-0000-0000-0000-000000000003',
    '55555555-5555-5555-5555-555555555555',
    7,
    'cm-e-2',
    'Copy that!',
    '2025-09-30 12:05:00+07'
  );

-- small conversation between khanh and leo created by khanh (DM-like)
insert into
  conversations (id, isGroup, title, created_by, created_at)
values
  (
    '66666666-0000-0000-0000-000000000006',
    false,
    null,
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '2025-09-02 09:01:00+07'
  );

insert into
  conversation_members (conversation_id, user_id, role, joined_at)
values
  (
    '66666666-0000-0000-0000-000000000006',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    'member',
    '2025-09-02 09:01:00+07'
  ),
  (
    '66666666-0000-0000-0000-000000000006',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    'member',
    '2025-09-02 09:01:10+07'
  );

insert into
  messages (
    id,
    conversation_id,
    sender_id,
    conversation_seq,
    client_message_id,
    content,
    created_at
  )
values
  (
    '66666666-0000-0000-0000-000000000062',
    '66666666-0000-0000-0000-000000000006',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    1,
    'cm-k-l-1',
    'Hey Leo, ready for code review?',
    '2025-09-02 09:02:00+07'
  ),
  (
    '66666666-0000-0000-0000-000000000063',
    '66666666-0000-0000-0000-000000000006',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    2,
    'cm-l-k-1',
    'Yes, send the link.',
    '2025-09-02 09:03:00+07'
  );

  drop function get_user_conversations (uuid);

CREATE OR REPLACE FUNCTION get_user_conversations(p_user_id UUID)
RETURNS TABLE (
    conversation_id UUID,
    is_group BOOLEAN,
    conversation_name TEXT,
    last_message TEXT,
    last_message_at TIMESTAMPTZ,
    last_read_at TIMESTAMPTZ
) LANGUAGE plpgsql 
SET search_path = public, pg_catalog AS $$
BEGIN
  RETURN QUERY
  SELECT
    c.id AS conversation_id,
    c.isgroup AS is_group,
    -- If group, use title, else peer's display name
    CASE
      WHEN c.isgroup THEN c.title
      ELSE COALESCE(peer.display_name, 'Unknown')
    END AS conversation_name,
    last_msg.content AS last_message,
    last_msg.created_at AS last_message_at,
    cm.last_read_at
  FROM conversation_members cm
  JOIN conversations c ON cm.conversation_id = c.id

  -- Peer info for private chats
  LEFT JOIN LATERAL (
    SELECT u.id, u.display_name
    FROM conversation_members cm2
    JOIN users u ON u.id = cm2.user_id
    WHERE cm2.conversation_id = c.id
      AND cm2.user_id <> p_user_id
    LIMIT 1
  ) peer ON (NOT c.isgroup)

  -- Last message info
  LEFT JOIN LATERAL (
    SELECT m.content, m.created_at
    FROM messages m
    WHERE m.conversation_id = c.id AND NOT m.is_deleted
    ORDER BY m.created_at DESC
    LIMIT 1
  ) last_msg ON TRUE

  WHERE cm.user_id = p_user_id
  ORDER BY COALESCE(last_msg.created_at, c.created_at) DESC;
END;
$$;
