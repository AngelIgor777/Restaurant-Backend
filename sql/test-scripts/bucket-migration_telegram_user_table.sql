BEGIN;

-- 1) Посмотреть, какие строки попадут под обновление
SELECT id, photo_url
FROM restaurant_service.restaurant_service.telegram_user
WHERE photo_url LIKE
      'https://s3.timeweb.cloud/cf1b889c-51893717-bc35-4427-a93b-2be350132697%';

-- 2) Обновить префикс URL-а
UPDATE restaurant_service.restaurant_service.telegram_user
SET photo_url = replace(
        photo_url,
        'https://s3.timeweb.cloud/cf1b889c-51893717-bc35-4427-a93b-2be350132697',
        'https://s3.hostman.com/100afe20-f0798e42-e226-429d-ae47-d59cd7e5ebe4'
                )
WHERE photo_url LIKE
      'https://s3.timeweb.cloud/cf1b889c-51893717-bc35-4427-a93b-2be350132697%';

-- 3) Убедиться, что URL-ы поменялись
SELECT id, photo_url
FROM restaurant_service.restaurant_service.telegram_user
WHERE photo_url LIKE
      'https://s3.hostman.com/100afe20-f0798e42-e226-429d-ae47-d59cd7e5ebe4%';

COMMIT;