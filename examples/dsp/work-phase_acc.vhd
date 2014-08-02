library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity phase_acc is
  generic (width_in : integer := 26;
           width_out : integer := 8);
  port (rst : in std_logic;
        clk : in std_logic;
        mult : in unsigned(width_in - 1 downto 0);
        phase : out unsigned(width_out - 1 downto 0));
end;

architecture behavioral of phase_acc is
  signal s_phase : unsigned(width_in - 1 downto 0);
begin
  phase <= s_phase(width_in - 1 downto width_in - width_out);
  process (rst, clk)
  begin
    if rst = '1' then
      s_phase <= (others => '0');
    elsif clk'event and clk = '1' then
      s_phase <= s_phase + mult;
    end if;
  end process ;
end;

