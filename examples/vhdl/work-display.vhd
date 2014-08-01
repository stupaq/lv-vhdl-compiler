library ieee;
use ieee.std_logic_1164.ALL;
use ieee.numeric_std.ALL;

entity display is
    port (input : in std_logic_vector(15 downto 0);
        clk : in std_logic;
        active : in std_logic;
        seg : out std_logic_vector(7 downto 0);
        an : out std_logic_vector(3 downto 0));
end;

architecture behavioral of display is
    signal s_seg : unsigned(1 downto 0) := "00";
    type SEGMENT is array (3 downto 0) of std_logic_vector(3 downto 0);
    type DIGIT is array (9 downto 0) of std_logic_vector(6 downto 0);
    constant SEGMENTS : SEGMENT := ("0111", "1011", "1101", "1110");
    constant DIGITS : DIGIT := ("0010000", -- 9
    "0000000", -- 8
    "1111000", -- 7
    "0000010", -- 6
    "0010010", -- 5
    "0011001", -- 4
    "0110000", -- 3
    "0100100", -- 2
    "1111001", -- 1
    "1000000");
begin
    process (clk)
    begin
        if clk'event and clk = '1' then
            s_seg <= s_seg + "01";
            an <= SEGMENTS(TO_INTEGER(s_seg));
            seg(6 downto 0) <= DIGITS(TO_INTEGER(unsigned(input(TO_INTEGER(
                s_seg) * 4 + 3 downto TO_INTEGER(s_seg) * 4))));
            case s_seg is
                when "10" => seg(7) <= '0';
                when "00" => seg(7) <= not active;
                when others => seg(7) <= '1';
            end case;
        end if;
    end process ;
end;

