// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

/**
 * @title VibeAgentRegistry
 * @dev On-chain registry for VibeAgent - AI Financial Assistant on BNB Chain
 * @notice Users can register their wallet and the contract tracks total registrations
 */
contract VibeAgentRegistry {

    string public constant APP_NAME = "VibeAgent";
    string public constant APP_VERSION = "1.0.0";
    string public constant DESCRIPTION = "AI-Powered Financial Assistant for BNB Chain";

    address public owner;
    uint256 public totalUsers;

    struct UserProfile {
        bool isRegistered;
        uint256 registeredAt;
        string nickname;
    }

    mapping(address => UserProfile) public users;

    event UserRegistered(address indexed user, string nickname, uint256 timestamp);
    event UserUpdated(address indexed user, string nickname, uint256 timestamp);

    modifier onlyOwner() {
        require(msg.sender == owner, "Only owner");
        _;
    }

    constructor() {
        owner = msg.sender;
    }

    /**
     * @dev Register as a VibeAgent user on-chain
     * @param _nickname User's display name
     */
    function register(string calldata _nickname) external {
        require(!users[msg.sender].isRegistered, "Already registered");
        require(bytes(_nickname).length > 0 && bytes(_nickname).length <= 32, "Nickname 1-32 chars");

        users[msg.sender] = UserProfile({
            isRegistered: true,
            registeredAt: block.timestamp,
            nickname: _nickname
        });

        totalUsers++;

        emit UserRegistered(msg.sender, _nickname, block.timestamp);
    }

    /**
     * @dev Update nickname
     * @param _nickname New display name
     */
    function updateNickname(string calldata _nickname) external {
        require(users[msg.sender].isRegistered, "Not registered");
        require(bytes(_nickname).length > 0 && bytes(_nickname).length <= 32, "Nickname 1-32 chars");

        users[msg.sender].nickname = _nickname;

        emit UserUpdated(msg.sender, _nickname, block.timestamp);
    }

    /**
     * @dev Check if an address is registered
     */
    function isRegistered(address _user) external view returns (bool) {
        return users[_user].isRegistered;
    }

    /**
     * @dev Get user profile
     */
    function getProfile(address _user) external view returns (bool registered, uint256 registeredAt, string memory nickname) {
        UserProfile memory profile = users[_user];
        return (profile.isRegistered, profile.registeredAt, profile.nickname);
    }
}
