/*
 * Copyright (c) Interactive Information R & D (I2RD) LLC.
 * All Rights Reserved.
 *
 * This software is confidential and proprietary information of
 * I2RD LLC ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered
 * into with I2RD.
 */

package net.venturetech.sshd_test.config.automation.schedule

import net.proteusframework.core.automation.DataConversion
import net.proteusframework.core.automation.SQLDataConversion
import net.proteusframework.core.automation.TaskQualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile

/**
 * Data covnersions for schedule API.
 * @author Russ Tennant (russ@venturetech.net)
 */
@Profile(["automation", "development"])
@Configuration
@Lazy
class ScheduleDataConversion1
{
    private static final String IDENTIFIER = "sshd-test-schedule"

    /**
     * Add Schedule
     * 2017.01.11 at 22:28 UTC
     * @return Bean.
     */
    @TaskQualifier(TaskQualifier.Type.data_conversion)
    @Bean
    DataConversion dataConversion_201701112228()
    {
        def ddl = [
            $/create table app.Schedule (schedule_id int4 not null, disc_type varchar(255) not null, 
createtime timestamp not null, createuser int8, lastmodtime timestamp not null, lastmoduser int8, primary key (schedule_id))/$,
            $/create table app.ical4jschedule (ICal4jSchedule_id int4 not null, eventprogrammaticidentifier varchar(255), 
repeat boolean, recurrencerule varchar(255), temporaldirection varchar(255), primary key (ICal4jSchedule_id))/$,
            $/create table app.relativeperiodschedule (RelativePeriodSchedule_id int4 not null, 
eventprogrammaticidentifier varchar(255), repeat boolean, period varchar(255), time time, 
temporaldirection varchar(255), primary key (RelativePeriodSchedule_id))/$,
            $/create table audit.Schedule_AUD (disc_type varchar(255) not null, schedule_id int4 not null, 
REV int4 not null, revtype int2, primary key (schedule_id, REV))/$,
            $/create table audit.ical4jschedule_AUD (ICal4jSchedule_id int4 not null, REV int4 not null, 
eventprogrammaticidentifier varchar(255), repeat boolean, recurrencerule varchar(255), temporaldirection int4, 
primary key (ICal4jSchedule_id, REV))/$,
            $/create table audit.relativeperiodschedule_AUD (RelativePeriodSchedule_id int4 not null, REV int4 not null, 
eventprogrammaticidentifier varchar(255), repeat boolean, period varchar(255), time time, 
temporaldirection int4, primary key (RelativePeriodSchedule_id, REV))/$,
            $/create index schedule_disc_idx on app.Schedule (disc_type)/$,
            $/alter table app.Schedule add constraint FK_77hcm4h5i2ws4yucjnwru69x3 foreign key (createuser) 
references Role/$,
            $/alter table app.Schedule add constraint FK_tfovuod0gt382elqocm4tqaj7 foreign key (lastmoduser) 
references Role/$,
            $/alter table app.ical4jschedule add constraint FK_kv3cgqtbsnq8nww9pao5lvm7v foreign key (ICal4jSchedule_id) 
references app.Schedule/$,
            $/alter table app.relativeperiodschedule add constraint FK_dc3167603jugwpp96g2fddvxt 
foreign key (RelativePeriodSchedule_id) references app.Schedule/$,
            $/alter table audit.Schedule_AUD add constraint FK_dasyt80y84r7yp28nwmw8k7ev foreign key (REV) 
references audit.REVINFO/$,
            $/alter table audit.ical4jschedule_AUD add constraint FK_jeh5q2v29ikcuco5altja5sov foreign key (ICal4jSchedule_id, REV)
 references audit.Schedule_AUD/$,
            $/alter table audit.relativeperiodschedule_AUD add constraint FK_78wtb0hshvemw4px7mn2t8aip 
foreign key (RelativePeriodSchedule_id, REV) references audit.Schedule_AUD/$,
            $/create sequence app.schedule_id_seq start 1 increment 5/$,
        ]
        new SQLDataConversion(IDENTIFIER, 'Add Schedule', 201701112228, false, null, ddl, null, null)
    }
}
