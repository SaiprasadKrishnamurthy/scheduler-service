package com.fp.scheduler.rest;

import com.fp.scheduler.model.JobType;
import com.fp.scheduler.model.ScheduledJobInfo;
import com.fp.scheduler.model.TriggerStatus;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>REST controller for managing various scheduled jobs in the platform.</p>
 *
 * @author Sai.
 */
@Slf4j
@RequestMapping("/api/v1")
@RestController
public class SchedulerController {
    private final SchedulerFactoryBean schedulerFactoryBean;

    public SchedulerController(final SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    @GetMapping(value = "/scheduled-jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllScheduledJobs() throws Exception {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<?> jobDetails = jobKeys.stream()
                .map(jobKey -> {
                    try {
                        return new Pair<>(scheduler.getJobDetail(jobKey), scheduler.getTriggersOfJob(jobKey));
                    } catch (SchedulerException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(p -> new ScheduledJobInfo<>(p.getValue0(), p.getValue1()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(jobDetails);
    }

    @PutMapping(value = "/daily-schedule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> scheduleDailyAt(@RequestParam("jobType") final JobType jobType,
                                             @RequestParam("jobSourceId") final String jobSourceId,
                                             @ApiParam(name = "timezone", allowableValues = "Africa/Abidjan, Africa/Accra, Africa/Addis_Ababa, Africa/Algiers, Africa/Asmara, Africa/Asmera, Africa/Bamako, Africa/Bangui, Africa/Banjul, Africa/Bissau, Africa/Blantyre, Africa/Brazzaville, Africa/Bujumbura, Africa/Cairo, Africa/Casablanca, Africa/Ceuta, Africa/Conakry, Africa/Dakar, Africa/Dar_es_Salaam, Africa/Djibouti, Africa/Douala, Africa/El_Aaiun, Africa/Freetown, Africa/Gaborone, Africa/Harare, Africa/Johannesburg, Africa/Juba, Africa/Kampala, Africa/Khartoum, Africa/Kigali, Africa/Kinshasa, Africa/Lagos, Africa/Libreville, Africa/Lome, Africa/Luanda, Africa/Lubumbashi, Africa/Lusaka, Africa/Malabo, Africa/Maputo, Africa/Maseru, Africa/Mbabane, Africa/Mogadishu, Africa/Monrovia, Africa/Nairobi, Africa/Ndjamena, Africa/Niamey, Africa/Nouakchott, Africa/Ouagadougou, Africa/Porto-Novo, Africa/Sao_Tome, Africa/Timbuktu, Africa/Tripoli, Africa/Tunis, Africa/Windhoek, America/Adak, America/Anchorage, America/Anguilla, America/Antigua, America/Araguaina, America/Argentina/Buenos_Aires, America/Argentina/Catamarca, America/Argentina/ComodRivadavia, America/Argentina/Cordoba, America/Argentina/Jujuy, America/Argentina/La_Rioja, America/Argentina/Mendoza, America/Argentina/Rio_Gallegos, America/Argentina/Salta, America/Argentina/San_Juan, America/Argentina/San_Luis, America/Argentina/Tucuman, America/Argentina/Ushuaia, America/Aruba, America/Asuncion, America/Atikokan, America/Atka, America/Bahia, America/Bahia_Banderas, America/Barbados, America/Belem, America/Belize, America/Blanc-Sablon, America/Boa_Vista, America/Bogota, America/Boise, America/Buenos_Aires, America/Cambridge_Bay, America/Campo_Grande, America/Cancun, America/Caracas, America/Catamarca, America/Cayenne, America/Cayman, America/Chicago, America/Chihuahua, America/Coral_Harbour, America/Cordoba, America/Costa_Rica, America/Creston, America/Cuiaba, America/Curacao, America/Danmarkshavn, America/Dawson, America/Dawson_Creek, America/Denver, America/Detroit, America/Dominica, America/Edmonton, America/Eirunepe, America/El_Salvador, America/Ensenada, America/Fort_Nelson, America/Fort_Wayne, America/Fortaleza, America/Glace_Bay, America/Godthab, America/Goose_Bay, America/Grand_Turk, America/Grenada, America/Guadeloupe, America/Guatemala, America/Guayaquil, America/Guyana, America/Halifax, America/Havana, America/Hermosillo, America/Indiana/Indianapolis, America/Indiana/Knox, America/Indiana/Marengo, America/Indiana/Petersburg, America/Indiana/Tell_City, America/Indiana/Vevay, America/Indiana/Vincennes, America/Indiana/Winamac, America/Indianapolis, America/Inuvik, America/Iqaluit, America/Jamaica, America/Jujuy, America/Juneau, America/Kentucky/Louisville, America/Kentucky/Monticello, America/Knox_IN, America/Kralendijk, America/La_Paz, America/Lima, America/Los_Angeles, America/Louisville, America/Lower_Princes, America/Maceio, America/Managua, America/Manaus, America/Marigot, America/Martinique, America/Matamoros, America/Mazatlan, America/Mendoza, America/Menominee, America/Merida, America/Metlakatla, America/Mexico_City, America/Miquelon, America/Moncton, America/Monterrey, America/Montevideo, America/Montreal, America/Montserrat, America/Nassau, America/New_York, America/Nipigon, America/Nome, America/Noronha, America/North_Dakota/Beulah, America/North_Dakota/Center, America/North_Dakota/New_Salem, America/Ojinaga, America/Panama, America/Pangnirtung, America/Paramaribo, America/Phoenix, America/Port-au-Prince, America/Port_of_Spain, America/Porto_Acre, America/Porto_Velho, America/Puerto_Rico, America/Punta_Arenas, America/Rainy_River, America/Rankin_Inlet, America/Recife, America/Regina, America/Resolute, America/Rio_Branco, America/Rosario, America/Santa_Isabel, America/Santarem, America/Santiago, America/Santo_Domingo, America/Sao_Paulo, America/Scoresbysund, America/Shiprock, America/Sitka, America/St_Barthelemy, America/St_Johns, America/St_Kitts, America/St_Lucia, America/St_Thomas, America/St_Vincent, America/Swift_Current, America/Tegucigalpa, America/Thule, America/Thunder_Bay, America/Tijuana, America/Toronto, America/Tortola, America/Vancouver, America/Virgin, America/Whitehorse, America/Winnipeg, America/Yakutat, America/Yellowknife, Antarctica/Casey, Antarctica/Davis, Antarctica/DumontDUrville, Antarctica/Macquarie, Antarctica/Mawson, Antarctica/McMurdo, Antarctica/Palmer, Antarctica/Rothera, Antarctica/South_Pole, Antarctica/Syowa, Antarctica/Troll, Antarctica/Vostok, Arctic/Longyearbyen, Asia/Aden, Asia/Almaty, Asia/Amman, Asia/Anadyr, Asia/Aqtau, Asia/Aqtobe, Asia/Ashgabat, Asia/Ashkhabad, Asia/Atyrau, Asia/Baghdad, Asia/Bahrain, Asia/Baku, Asia/Bangkok, Asia/Barnaul, Asia/Beirut, Asia/Bishkek, Asia/Brunei, Asia/Calcutta, Asia/Chita, Asia/Choibalsan, Asia/Chongqing, Asia/Chungking, Asia/Colombo, Asia/Dacca, Asia/Damascus, Asia/Dhaka, Asia/Dili, Asia/Dubai, Asia/Dushanbe, Asia/Famagusta, Asia/Gaza, Asia/Harbin, Asia/Hebron, Asia/Ho_Chi_Minh, Asia/Hong_Kong, Asia/Hovd, Asia/Irkutsk, Asia/Istanbul, Asia/Jakarta, Asia/Jayapura, Asia/Jerusalem, Asia/Kabul, Asia/Kamchatka, Asia/Karachi, Asia/Kashgar, Asia/Kathmandu, Asia/Katmandu, Asia/Khandyga, Asia/Kolkata, Asia/Krasnoyarsk, Asia/Kuala_Lumpur, Asia/Kuching, Asia/Kuwait, Asia/Macao, Asia/Macau, Asia/Magadan, Asia/Makassar, Asia/Manila, Asia/Muscat, Asia/Nicosia, Asia/Novokuznetsk, Asia/Novosibirsk, Asia/Omsk, Asia/Oral, Asia/Phnom_Penh, Asia/Pontianak, Asia/Pyongyang, Asia/Qatar, Asia/Qostanay, Asia/Qyzylorda, Asia/Rangoon, Asia/Riyadh, Asia/Saigon, Asia/Sakhalin, Asia/Samarkand, Asia/Seoul, Asia/Shanghai, Asia/Singapore, Asia/Srednekolymsk, Asia/Taipei, Asia/Tashkent, Asia/Tbilisi, Asia/Tehran, Asia/Tel_Aviv, Asia/Thimbu, Asia/Thimphu, Asia/Tokyo, Asia/Tomsk, Asia/Ujung_Pandang, Asia/Ulaanbaatar, Asia/Ulan_Bator, Asia/Urumqi, Asia/Ust-Nera, Asia/Vientiane, Asia/Vladivostok, Asia/Yakutsk, Asia/Yangon, Asia/Yekaterinburg, Asia/Yerevan, Atlantic/Azores, Atlantic/Bermuda, Atlantic/Canary, Atlantic/Cape_Verde, Atlantic/Faeroe, Atlantic/Faroe, Atlantic/Jan_Mayen, Atlantic/Madeira, Atlantic/Reykjavik, Atlantic/South_Georgia, Atlantic/St_Helena, Atlantic/Stanley, Australia/ACT, Australia/Adelaide, Australia/Brisbane, Australia/Broken_Hill, Australia/Canberra, Australia/Currie, Australia/Darwin, Australia/Eucla, Australia/Hobart, Australia/LHI, Australia/Lindeman, Australia/Lord_Howe, Australia/Melbourne, Australia/NSW, Australia/North, Australia/Perth, Australia/Queensland, Australia/South, Australia/Sydney, Australia/Tasmania, Australia/Victoria, Australia/West, Australia/Yancowinna, Brazil/Acre, Brazil/DeNoronha, Brazil/East, Brazil/West, CET, CST6CDT, Canada/Atlantic, Canada/Central, Canada/Eastern, Canada/Mountain, Canada/Newfoundland, Canada/Pacific, Canada/Saskatchewan, Canada/Yukon, Chile/Continental, Chile/EasterIsland, Cuba, EET, EST5EDT, Egypt, Eire, Etc/GMT, Etc/GMT+0, Etc/GMT+1, Etc/GMT+10, Etc/GMT+11, Etc/GMT+12, Etc/GMT+2, Etc/GMT+3, Etc/GMT+4, Etc/GMT+5, Etc/GMT+6, Etc/GMT+7, Etc/GMT+8, Etc/GMT+9, Etc/GMT-0, Etc/GMT-1, Etc/GMT-10, Etc/GMT-11, Etc/GMT-12, Etc/GMT-13, Etc/GMT-14, Etc/GMT-2, Etc/GMT-3, Etc/GMT-4, Etc/GMT-5, Etc/GMT-6, Etc/GMT-7, Etc/GMT-8, Etc/GMT-9, Etc/GMT0, Etc/Greenwich, Etc/UCT, Etc/UTC, Etc/Universal, Etc/Zulu, Europe/Amsterdam, Europe/Andorra, Europe/Astrakhan, Europe/Athens, Europe/Belfast, Europe/Belgrade, Europe/Berlin, Europe/Bratislava, Europe/Brussels, Europe/Bucharest, Europe/Budapest, Europe/Busingen, Europe/Chisinau, Europe/Copenhagen, Europe/Dublin, Europe/Gibraltar, Europe/Guernsey, Europe/Helsinki, Europe/Isle_of_Man, Europe/Istanbul, Europe/Jersey, Europe/Kaliningrad, Europe/Kiev, Europe/Kirov, Europe/Lisbon, Europe/Ljubljana, Europe/London, Europe/Luxembourg, Europe/Madrid, Europe/Malta, Europe/Mariehamn, Europe/Minsk, Europe/Monaco, Europe/Moscow, Europe/Nicosia, Europe/Oslo, Europe/Paris, Europe/Podgorica, Europe/Prague, Europe/Riga, Europe/Rome, Europe/Samara, Europe/San_Marino, Europe/Sarajevo, Europe/Saratov, Europe/Simferopol, Europe/Skopje, Europe/Sofia, Europe/Stockholm, Europe/Tallinn, Europe/Tirane, Europe/Tiraspol, Europe/Ulyanovsk, Europe/Uzhgorod, Europe/Vaduz, Europe/Vatican, Europe/Vienna, Europe/Vilnius, Europe/Volgograd, Europe/Warsaw, Europe/Zagreb, Europe/Zaporozhye, Europe/Zurich, GB, GB-Eire, GMT, GMT0, Greenwich, Hongkong, Iceland, Indian/Antananarivo, Indian/Chagos, Indian/Christmas, Indian/Cocos, Indian/Comoro, Indian/Kerguelen, Indian/Mahe, Indian/Maldives, Indian/Mauritius, Indian/Mayotte, Indian/Reunion, Iran, Israel, Jamaica, Japan, Kwajalein, Libya, MET, MST7MDT, Mexico/BajaNorte, Mexico/BajaSur, Mexico/General, NZ, NZ-CHAT, Navajo, PRC, PST8PDT, Pacific/Apia, Pacific/Auckland, Pacific/Bougainville, Pacific/Chatham, Pacific/Chuuk, Pacific/Easter, Pacific/Efate, Pacific/Enderbury, Pacific/Fakaofo, Pacific/Fiji, Pacific/Funafuti, Pacific/Galapagos, Pacific/Gambier, Pacific/Guadalcanal, Pacific/Guam, Pacific/Honolulu, Pacific/Johnston, Pacific/Kiritimati, Pacific/Kosrae, Pacific/Kwajalein, Pacific/Majuro, Pacific/Marquesas, Pacific/Midway, Pacific/Nauru, Pacific/Niue, Pacific/Norfolk, Pacific/Noumea, Pacific/Pago_Pago, Pacific/Palau, Pacific/Pitcairn, Pacific/Pohnpei, Pacific/Ponape, Pacific/Port_Moresby, Pacific/Rarotonga, Pacific/Saipan, Pacific/Samoa, Pacific/Tahiti, Pacific/Tarawa, Pacific/Tongatapu, Pacific/Truk, Pacific/Wake, Pacific/Wallis, Pacific/Yap, Poland, Portugal, ROK, Singapore, SystemV/AST4, SystemV/AST4ADT, SystemV/CST6, SystemV/CST6CDT, SystemV/EST5, SystemV/EST5EDT, SystemV/HST10, SystemV/MST7, SystemV/MST7MDT, SystemV/PST8, SystemV/PST8PDT, SystemV/YST9, SystemV/YST9YDT, Turkey, UCT, US/Alaska, US/Aleutian, US/Arizona, US/Central, US/East-Indiana, US/Eastern, US/Hawaii, US/Indiana-Starke, US/Michigan, US/Mountain, US/Pacific, US/Pacific-New, US/Samoa, UTC, Universal, W-SU, WET, Zulu, EST, HST, MST, ACT, AET, AGT, ART, AST, BET, BST, CAT, CNT, CST, CTT, EAT, ECT, IET, IST, JST, MIT, NET, NST, PLT, PNT, PRT, PST, SST, VST")
                                             @RequestParam(value = "timezone", defaultValue = "UTC") final String timezone,
                                             final @RequestParam("time") String time) throws Exception {
        String pattern = "(([0-1][0-9])|([2][0-3])):([0-5][0-9])";
        String cronTemplate = "0 %s %s ? * *";
        if (!time.matches(pattern)) {
            return ResponseEntity.badRequest().body(ImmutableMap.of("message", "The 'time' should be of the format hours24:minutes. eg: 14:20, 03:00"));
        }
        return saveOrUpdate(jobType, jobSourceId, String.format(cronTemplate, time.split(":")[1], time.split(":")[0]), timezone);
    }

    private ResponseEntity<?> saveOrUpdate(final JobType jobType, final String jobSourceId, final String cronExpression, String timezone) throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.jobGroupEquals(jobType.name());
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        // Delete the existing schedule.
        jobKeys.forEach(jobKey -> {
            try {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                if (jobType.name().equals(jobDetail.getJobDataMap().get("jobType"))
                        && jobSourceId.equals(jobDetail.getJobDataMap().get("jobSourceId"))) {
                    scheduler.deleteJob(jobKey);
                }
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        });
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobType", jobType.name());
        jobDataMap.put("jobSourceId", jobSourceId);
        JobDetail jobDetail = JobBuilder.newJob(jobType.getQuartzJobBeanClass())
                .withIdentity(UUID.randomUUID().toString(), jobType.name())
                .withDescription(jobType.getDescription())
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), jobDetail.getKey().getGroup())
                .withDescription(jobType.getDescription())
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression).inTimeZone(TimeZone.getTimeZone(timezone)))
                .build();
        schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
        ScheduledJobInfo<CronTrigger> scheduledJobInfo = new ScheduledJobInfo<>(jobDetail, Collections.singletonList(trigger));
        return ResponseEntity.status(jobKeys.isEmpty() ? HttpStatus.CREATED : HttpStatus.OK).body(scheduledJobInfo);
    }

    @PutMapping(value = "/pause-job/{jobKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> pauseJob(@PathVariable("jobKey") final String jobKey) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Optional<JobKey> job = findJob(jobKey);
        if (job.isPresent()) {
            scheduler.pauseJob(job.get());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Done.");
    }

    @PutMapping(value = "/resume-job/{jobKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resumeJob(@PathVariable("jobKey") final String jobKey) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Optional<JobKey> job = findJob(jobKey);
        if (job.isPresent()) {
            scheduler.resumeJob(job.get());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Done.");
    }

    @PutMapping(value = "/delete-job/{jobKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteJob(@PathVariable("jobKey") final String jobKey) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Optional<JobKey> job = findJob(jobKey);
        if (job.isPresent()) {
            scheduler.deleteJob(job.get());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Done.");
    }

    @GetMapping(value = "/triggers-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTriggerStatus() throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
        List<TriggerStatus> triggerStatus = new ArrayList<>();
        jobKeys.stream()
                .forEach(jk -> {
                    try {
                        JobDetail jobDetail = scheduler.getJobDetail(jk);
                        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jk);
                        triggers.stream().forEach(trigger -> {
                            try {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date triggerTime = trigger.getNextFireTime();
                                String dateStr = simpleDateFormat.format(triggerTime);
                                triggerStatus.add((new TriggerStatus(jk.getGroup(), jk.getName(), jobDetail.getJobDataMap().get("jobSourceId").toString()
                                        , scheduler.getTriggerState(trigger.getKey()).toString(), dateStr)));
                            } catch (SchedulerException e) {
                                e.printStackTrace();
                            }
                        });

                    } catch (SchedulerException e) {
                        e.printStackTrace();
                    }

                });
        return ResponseEntity.status(HttpStatus.OK).body(triggerStatus);
    }

    private Optional<JobKey> findJob(final String jobKey) throws SchedulerException {
        return schedulerFactoryBean.getScheduler().getJobKeys(GroupMatcher.anyJobGroup()).stream()
                .filter(jk -> jk.getName().equalsIgnoreCase(jobKey))
                .findFirst();
    }

}
