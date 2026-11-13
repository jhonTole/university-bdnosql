db = db.getSiblingDB('universitydb');

// AL ser un proyecto en conjunto colocamos datos de referencia de cada tabla para coincidir al ejecutar el proyecto

// IDs de referencia
const venueId = new ObjectId('6af3523813485c6bcc18ce89');
const eventId = new ObjectId('6af3508413485c6bcc18ce7d');
const attendeeId = new ObjectId('6af34ef513485c6bcc18ce78');
const registrationId = new ObjectId('6af3528113485c6bcc18ce8c');

// 1. Venues
db.venues.updateOne(
    { _id: venueId },
    {
        $setOnInsert: {
            _id: venueId,
            name: 'Auditorio Mayor',
            type: 'Auditorio',
            capacity: 150
        }
    },
    { upsert: true }
);

// 2. Events
db.events.updateOne(
    { _id: eventId },
    {
        $setOnInsert: {
            _id: eventId,
            name: 'Cumbre 5.0',
            description: 'Evento de ingenierias donde ..',
            category: 'Academico',
            date: new ISODate('2026-11-20T00:00:00.000Z'),
            startTime: '08:00',
            endTime: '12:00',
            capacity: 100,
            venueId: venueId
        }
    },
    { upsert: true }
);

// 3. Attendees
db.attendees.updateOne(
    { _id: attendeeId },
    {
        $setOnInsert: {
            _id: attendeeId,
            name: 'John Doe',
            documentNumber: '123456789',
            email: 'john@ustatunja.edu.co'
        }
    },
    { upsert: true }
);

// 4. Registrations
db.registrations.updateOne(
    { _id: registrationId },
    {
        $setOnInsert: {
            _id: registrationId,
            attendeeId: attendeeId,
            eventId: eventId,
            registrationDate: new ISODate('2026-11-03T00:00:00.000Z'),
            status: 'Activa'
        }
    },
    { upsert: true }
);

