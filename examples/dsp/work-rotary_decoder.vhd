library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity rotary_decoder is
    port (signal rst : in std_logic;
        signal clk : in std_logic;
        signal rot_a : in std_logic;
        signal rot_b : in std_logic;
        signal rot_change : out signed(1 downto 0));
end;

architecture behavioral of rotary_decoder is
    signal prev_rot_ab : std_logic_vector(1 downto 0);
    signal trigger : std_logic;
    signal prev_trigger : std_logic;
    signal dir_left : std_logic;
begin
    event_encoder : process (rst, clk)
        variable state : std_logic_vector(2 downto 0);
    begin
        if rst = '1' then
            prev_trigger <= '0';
            rot_change <= "00";
        elsif clk'event and clk = '1' then
            prev_trigger <= trigger;
            state := prev_trigger & trigger & dir_left;
            case state is
                when "011" => rot_change <= "11";
                when "010" => rot_change <= "01";
                when others => rot_change <= "00";
            end case;
        end if;
    end process ;
    rotation_decoder : process (rst, clk)
    begin
        if rst = '1' then
            prev_rot_ab <= rot_b & rot_a;
            trigger <= '0';
            dir_left <= '0';
        elsif clk'event and clk = '1' then
            prev_rot_ab <= rot_a & rot_b;
            case prev_rot_ab is
                when "00" => trigger <= '1';
                when "01" => dir_left <= '1';
                when "10" => dir_left <= '0';
                when "11" => trigger <= '0';
                when others => trigger <= trigger;
            end case;
        end if;
    end process ;
end;

