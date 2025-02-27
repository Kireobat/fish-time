package eu.kireobat.fishtime

import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import javax.sql.DataSource

class TestDataLoaderUtil {

    private fun loadTestDataToDataSource(filePath: String, dataSource: DataSource) {
        ResourceDatabasePopulator(false, false, "UTF-8", ClassPathResource(filePath)).execute(dataSource)
    }
    fun cleanAllTestData(dataSource: DataSource) {
        loadTestDataToDataSource("db/delete_all_test_data.sql", dataSource)
        loadTestDataToDataSource("db/reset_all_sequences.sql", dataSource)
    }
    fun insertMeetings(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_meetings.sql", dataSource)
    }
    fun insertSystemUser(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_system_user.sql", dataSource)
    }
    fun insertOAuthProviders(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_oauth_providers.sql", dataSource)
    }
    fun insertParticipants(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_participants.sql", dataSource)
    }
    fun insertRoles(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_roles.sql", dataSource)
    }
    fun insertRooms(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_rooms.sql", dataSource)
    }
    fun insertUsers(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_users.sql", dataSource)
    }
    fun insertUsersMapRoles(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_users_map_roles.sql", dataSource)
    }
    fun syncSequences(dataSource: DataSource) {
        loadTestDataToDataSource("db/sync_sequences.sql", dataSource)
    }
}