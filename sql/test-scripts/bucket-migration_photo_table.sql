BEGIN;

SELECT id, url
FROM restaurant_service.restaurant_service.photo
WHERE url LIKE
      'https://s3.timeweb.cloud/cf1b889c-51893717-bc35-4427-a93b-2be350132697%';

UPDATE restaurant_service.restaurant_service.photo
SET url = replace(
        url,
        'https://s3.timeweb.cloud/cf1b889c-51893717-bc35-4427-a93b-2be350132697',
        'https://s3.hostman.com/100afe20-f0798e42-e226-429d-ae47-d59cd7e5ebe4'
                )
WHERE url LIKE
      'https://s3.timeweb.cloud/cf1b889c-51893717-bc35-4427-a93b-2be350132697%';

SELECT id, url
FROM restaurant_service.restaurant_service.photo
WHERE url LIKE
      'https://s3.hostman.com/100afe20-f0798e42-e226-429d-ae47-d59cd7e5ebe4%';

COMMIT;