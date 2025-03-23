package tictactoe.web.mapper;

import tictactoe.web.model.UserStatsDTO;

public class UserStatsDTOMapper {

    /**
     * Маппер статистики игр пользователя из domain в web
     *
     * @param domainUserStats Статистика игр пользователя из domain
     * @return Статистика игр пользователя из web
     */
    public static UserStatsDTO fromDomainToWeb(tictactoe.domain.model.UserStatsDTO domainUserStats) {
        UserStatsDTO webUserStats = new UserStatsDTO();
        webUserStats.setTotalGames(domainUserStats.getTotalGames());
        webUserStats.setPercentWinsGames(domainUserStats.getPercentWinsGames());
        webUserStats.setVsComputerGames(domainUserStats.getVsComputerGames());
        webUserStats.setWinsVsComputer(domainUserStats.getWinsVsComputer());
        webUserStats.setPercentWinsVsComputer(domainUserStats.getPercentWinsVsComputer());
        webUserStats.setLossesVsComputer(domainUserStats.getLossesVsComputer());
        webUserStats.setTiesVsComputer(domainUserStats.getTiesVsComputer());
        webUserStats.setVsHumanGames(domainUserStats.getVsHumanGames());
        webUserStats.setWinsVsHuman(domainUserStats.getWinsVsHuman());
        webUserStats.setPercentWinsVsHuman(domainUserStats.getPercentWinsVsHuman());
        webUserStats.setLossesVsHuman(domainUserStats.getLossesVsHuman());
        webUserStats.setTiesVsHuman(domainUserStats.getTiesVsHuman());
        return webUserStats;
    }

    /**
     * Маппер статистики игр пользователя из web в domain
     *
     * @param webUserStats Статистика игр пользователя из web
     * @return Статистика игр пользователя из domain
     */
    public static tictactoe.domain.model.UserStatsDTO fromWebToDomain(UserStatsDTO webUserStats) {
        tictactoe.domain.model.UserStatsDTO domainUserStats = new tictactoe.domain.model.UserStatsDTO();
        domainUserStats.setTotalGames(webUserStats.getTotalGames());
        domainUserStats.setPercentWinsGames(webUserStats.getPercentWinsGames());
        domainUserStats.setVsComputerGames(webUserStats.getVsComputerGames());
        domainUserStats.setWinsVsComputer(webUserStats.getWinsVsComputer());
        domainUserStats.setPercentWinsVsComputer(webUserStats.getPercentWinsVsComputer());
        domainUserStats.setTiesVsComputer(webUserStats.getTiesVsComputer());
        domainUserStats.setLossesVsComputer(webUserStats.getLossesVsComputer());
        domainUserStats.setVsHumanGames(webUserStats.getVsHumanGames());
        domainUserStats.setWinsVsHuman(webUserStats.getWinsVsHuman());
        domainUserStats.setPercentWinsVsHuman(webUserStats.getPercentWinsVsHuman());
        domainUserStats.setLossesVsHuman(webUserStats.getLossesVsHuman());
        domainUserStats.setTiesVsHuman(webUserStats.getTiesVsHuman());
        return domainUserStats;
    }
}
