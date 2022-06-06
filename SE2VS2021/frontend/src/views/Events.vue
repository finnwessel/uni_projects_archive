<template>
  <div>
    <div class="lg:p-5">
      <FullCalendar :events="eventsStore.getEvents" :options="options" />
    </div>
    <div>
      <ViewEventModal
        :display="displayViewEventModal"
        :selectedEvent="currentEvent"
        @delete="deleteEvent"
        @save="updateEvent"
        @close="displayViewEventModal = $event"
      ></ViewEventModal>
      <CreateEventModal
        :display="createEventModal.display"
        :select-info="createEventModal.selectInfo"
        @close="createEventModal.display = $event"
        @save="createNewEvent"
      ></CreateEventModal>
    </div>
  </div>
</template>

<script lang="ts">
import {
  CalendarOptions,
  EventApi,
  DateSelectArg,
  EventClickArg,
} from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import { defineComponent } from "vue";
import ViewEventModal from "@/components/events/ViewEventModal.vue";
import CreateEventModal from "@/components/events/CreateEventModal.vue";
import { useEventsStore } from "@/store/EventsStore";
import { NewEvent } from "@/interfaces/EventsInterfaces";
import { fixDate } from "@/helpers/date";
import EventService from "@/services/EventService";
export default defineComponent({
  name: "Events",
  components: {
    ViewEventModal,
    CreateEventModal,
  },
  setup() {
    const eventsStore = useEventsStore();
    eventsStore.loadEvents();
    return {
      eventsStore,
    };
  },
  data() {
    return {
      currentEvent: null,
      createEventModal: {
        display: false,
        selectInfo: null,
      },
      displayViewEventModal: false,
      options: {
        timeZone: "local",
        plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
        initialDate: Date.now(),
        headerToolbar: {
          left: "prev,next today",
          center: "title",
          right: "dayGridMonth,timeGridWeek,timeGridDay",
        },
        eventStartEditable: false,
        height: "auto",
        firstDay: 1,
        editable: true,
        selectable: true,
        weekends: true,
        selectMirror: true,
        dayMaxEvents: true,
        select: this.handleDateSelect,
        eventClick: this.handleEventClick,
        eventsSet: this.handleEvents,
      } as CalendarOptions,
      currentEvents: [] as EventApi[],
    };
  },
  beforeMount() {
    this.$emitter.on(
      "event:send-invite",
      (contactId: string, eventId: string) => {
        EventService.sendInvitation(contactId, eventId);
      }
    );
  },
  umounted() {
    this.$emitter.off("event:send-invite");
  },
  methods: {
    handleDateSelect(selectInfo: DateSelectArg): void {
      console.log(selectInfo);
      this.createEventModal.selectInfo = selectInfo;
      this.createEventModal.display = true;
    },
    handleEventClick(clickInfo: EventClickArg): void {
      this.currentEvent = clickInfo.event;
      this.displayViewEventModal = true;
    },
    handleEvents(events: EventApi[]): void {
      this.currentEvents = events;
    },
    updateEvent(evt) {
      this.eventsStore
        .updateEvent({
          id: evt.id,
          ownerId: evt.ownerId,
          title: evt.title,
          start: fixDate(evt.start),
          end: fixDate(evt.end),
          allDay: evt.allDay,
          description: evt.description,
        })
        .then(() => {
          this.$toast.add({
            severity: "success",
            summary: "Confirmed",
            detail: "Event updated",
            life: 1000,
          });
        })
        .catch(() => {
          this.$toast.add({
            severity: "error",
            summary: "Rejected",
            detail: "Failed to update event",
            life: 3000,
          });
        });
    },
    createNewEvent(evt) {
      this.eventsStore.addEvent({
        ownerId: evt.ownerId,
        title: evt.title,
        start: fixDate(evt.start),
        end: fixDate(evt.end),
        allDay: evt.allDay,
        description: evt.description,
      } as NewEvent);
    },
    deleteEvent(evt) {
      if (evt != null) {
        this.eventsStore
          .removeEvent(evt)
          .then(() => {
            evt.remove();
          }) // ToDo: throw toast
          .catch((err) => console.warn(err));
      }
      this.displayViewEventModal = false;
    },
  },
});
</script>

<style scoped>
@media screen and (max-width: 960px) {
  ::v-deep(.fc-header-toolbar) {
    display: flex;
    flex-wrap: wrap;
  }
}
</style>
